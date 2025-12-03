package com.nhnacademy.byeol23backend.bookset.book.service.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.nhnacademy.byeol23backend.bookset.book.domain.Book;
import com.nhnacademy.byeol23backend.bookset.book.domain.BookStatus;
import com.nhnacademy.byeol23backend.bookset.book.dto.BookCreateRequest;
import com.nhnacademy.byeol23backend.bookset.book.dto.BookInfoRequest;
import com.nhnacademy.byeol23backend.bookset.book.dto.BookOrderRequest;
import com.nhnacademy.byeol23backend.bookset.book.dto.BookResponse;
import com.nhnacademy.byeol23backend.bookset.book.dto.BookReview;
import com.nhnacademy.byeol23backend.bookset.book.dto.BookStockResponse;
import com.nhnacademy.byeol23backend.bookset.book.dto.BookStockUpdateRequest;
import com.nhnacademy.byeol23backend.bookset.book.dto.BookUpdateRequest;
import com.nhnacademy.byeol23backend.bookset.book.event.ViewCountIncreaseEvent;
import com.nhnacademy.byeol23backend.bookset.book.exception.BookNotFoundException;
import com.nhnacademy.byeol23backend.bookset.book.exception.ISBNAlreadyExistException;
import com.nhnacademy.byeol23backend.bookset.book.repository.BookRepository;
import com.nhnacademy.byeol23backend.bookset.book.service.BookService;
import com.nhnacademy.byeol23backend.bookset.bookcategory.domain.BookCategory;
import com.nhnacademy.byeol23backend.bookset.bookcategory.repository.BookCategoryRepository;
import com.nhnacademy.byeol23backend.bookset.bookcategory.service.BookCategoryService;
import com.nhnacademy.byeol23backend.bookset.bookcontributor.domain.BookContributor;
import com.nhnacademy.byeol23backend.bookset.bookcontributor.repository.BookContributorRepository;
import com.nhnacademy.byeol23backend.bookset.bookcontributor.service.BookContributorService;
import com.nhnacademy.byeol23backend.bookset.bookimage.domain.BookImage;
import com.nhnacademy.byeol23backend.bookset.bookimage.service.BookImageServiceImpl;
import com.nhnacademy.byeol23backend.bookset.booktag.domain.BookTag;
import com.nhnacademy.byeol23backend.bookset.booktag.repository.BookTagRepository;
import com.nhnacademy.byeol23backend.bookset.booktag.service.BookTagService;
import com.nhnacademy.byeol23backend.bookset.category.domain.Category;
import com.nhnacademy.byeol23backend.bookset.category.dto.CategoryLeafResponse;
import com.nhnacademy.byeol23backend.bookset.contributor.domain.Contributor;
import com.nhnacademy.byeol23backend.bookset.contributor.domain.dto.AllContributorResponse;
import com.nhnacademy.byeol23backend.bookset.contributor.repository.ContributorRepository;
import com.nhnacademy.byeol23backend.bookset.outbox.BookOutbox;
import com.nhnacademy.byeol23backend.bookset.outbox.event.BookOutboxEvent;
import com.nhnacademy.byeol23backend.bookset.outbox.repository.BookOutboxRepository;
import com.nhnacademy.byeol23backend.bookset.publisher.domain.Publisher;
import com.nhnacademy.byeol23backend.bookset.publisher.domain.dto.AllPublishersInfoResponse;
import com.nhnacademy.byeol23backend.bookset.publisher.exception.PublisherNotFoundException;
import com.nhnacademy.byeol23backend.bookset.publisher.repository.PublisherRepository;
import com.nhnacademy.byeol23backend.bookset.tag.domain.Tag;
import com.nhnacademy.byeol23backend.bookset.tag.domain.dto.AllTagsInfoResponse;
import com.nhnacademy.byeol23backend.cartset.cartbook.dto.CartOrderRequest;
import com.nhnacademy.byeol23backend.image.dto.GetUrlResponse;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class BookServiceImpl implements BookService {

	private final BookRepository bookRepository;
	private final PublisherRepository publisherRepository;
	private final ApplicationEventPublisher eventPublisher;
	private final BookCategoryRepository bookCategoryRepository;
	private final BookCategoryService bookCategoryService;
	private final BookContributorRepository bookContributorRepository;
	private final BookTagRepository bookTagRepository;
	private final BookTagService bookTagService;
	private final BookContributorService bookContributorService;
	private final BookImageServiceImpl bookImageService;
	private final BookOutboxRepository bookOutboxRepository;
	private final ContributorRepository contributorRepository;

	@Override
	@Transactional
	public BookResponse createBook(BookCreateRequest createRequest) {
		if (bookRepository.existsByIsbn(createRequest.isbn())) {
			throw new ISBNAlreadyExistException("이미 존재하는 ISBN입니다: " + createRequest.isbn());
		}
		Publisher publisher = publisherRepository.findById(createRequest.publisherId())
			.orElseThrow(() -> new PublisherNotFoundException("존재하지 않는 출판사 ID입니다: " + createRequest.publisherId()));

		Book book = new Book();
		book.createBook(createRequest, publisher);
		Book savedBook = bookRepository.save(book);
		bookCategoryService.createBookCategories(savedBook, createRequest.categoryIds());
		bookTagService.createBookTags(savedBook, createRequest.tagIds());
		bookContributorService.createBookContributors(savedBook, createRequest.contributorIds());
		log.info("새로운 도서가 생성되었습니다. ID: {}", savedBook.getBookId());

		BookOutbox savedOutBox = bookOutboxRepository.save(
			new BookOutbox(savedBook.getBookId(), BookOutbox.EventType.ADD));
		Long outboxId = savedOutBox.getId();

		log.info("[추가] 도서 아웃박스 이벤트 발행: {}", outboxId);
		eventPublisher.publishEvent(new BookOutboxEvent(outboxId));
		return toResponse(savedBook);
	}

	@Override
	public BookResponse getBook(Long bookId) {
		Book book = bookRepository.findById(bookId)
			.orElseThrow(() -> new BookNotFoundException("존재하지 않는 도서입니다: " + bookId));
		return toResponse(book);
	}

	@Override
	public BookStockResponse getBookStock(Long bookId) {
		Book book = bookRepository.findById(bookId)
			.orElseThrow(() -> new BookNotFoundException("존재하지 않는 도서입니다: " + bookId));

		return new BookStockResponse(book.getBookId(), book.getBookName(), book.getStock());
	}

	@Override
	public BookResponse getBookAndIncreaseViewCount(Long bookId, String viewerId) {
		Book book = bookRepository.findById(bookId)
			.orElseThrow(() -> new BookNotFoundException("존재하지 않는 도서입니다: " + bookId));
		eventPublisher.publishEvent(new ViewCountIncreaseEvent(bookId, viewerId));
		return toResponse(book);
	}

	@Override
	@Transactional
	public BookResponse updateBook(Long bookId, BookUpdateRequest updateRequest) {
		Book book = bookRepository.findById(bookId)
			.orElseThrow(() -> new BookNotFoundException("존재하지 않는 도서입니다: " + bookId));

		Publisher publisher = publisherRepository.findById(updateRequest.publisherId())
			.orElseThrow(() -> new PublisherNotFoundException("존재하지 않는 출판사 ID입니다: " + updateRequest.publisherId()));

		if (updateRequest.bookStatus() == BookStatus.SOLDOUT) {
			book.setStock(0);
			log.info("품절 시 재고 0 처리");
		}
		book.updateBook(updateRequest, publisher);
		bookCategoryService.updateBookCategories(book, updateRequest.categoryIds());
		bookTagService.updateBookTags(book, updateRequest.tagIds());
		bookContributorService.updateBookContributors(book, updateRequest.contributorIds());
		log.info("도서 정보가 수정되었습니다. ID: {}", book.getBookId());

		BookOutbox bookOutbox = bookOutboxRepository.save(new BookOutbox(bookId, BookOutbox.EventType.UPDATE));
		Long outboxId = bookOutbox.getId();

		log.info("[수정] 도서 아웃박스 이벤트 발행: {}", outboxId);
		eventPublisher.publishEvent(new BookOutboxEvent(outboxId));
		return toResponse(book);
	}

	@Override
	@Transactional
	public void updateBookStock(Long bookId, BookStockUpdateRequest request) {
		Book book = bookRepository.findById(bookId)
			.orElseThrow(() -> new BookNotFoundException("존재하지 않는 도서입니다: " + bookId));

		book.setStock(request.stock());
		log.info("도서 재고가 수정되었습니다. ID: {}", bookId);
	}

	@Override
	@Transactional
	public void deleteBook(Long bookId) {
		Book book = bookRepository.findById(bookId)
			.orElseThrow(() -> new BookNotFoundException("존재하지 않는 도서입니다: " + bookId));
		bookRepository.delete(book);
		bookCategoryRepository.deleteByBookId(bookId);
		bookTagRepository.deleteByBookId(bookId);
		bookContributorRepository.deleteByBookId(bookId);
		log.info("도서가 삭제 처리되었습니다. ID: {}", bookId);

		BookOutbox bookOutbox = bookOutboxRepository.save(new BookOutbox(bookId, BookOutbox.EventType.DELETE));
		Long outboxId = bookOutbox.getId();

		log.info("[삭제] 도서 아웃박스 이벤트 발행: {}", outboxId);
		eventPublisher.publishEvent(new BookOutboxEvent(outboxId));
	}

	@Override
	public Page<BookResponse> getBooks(Pageable pageable) {
		// Book만 페이징으로 조회 (Publisher는 ManyToOne이라 문제없음)
		Page<Book> bookPage = bookRepository.findAll(pageable);

		if (bookPage.isEmpty()) {
			return Page.empty(pageable);
		}

		List<Book> bookList = bookPage.getContent();
		List<Long> bookIds = bookList.stream()
			.map(Book::getBookId)
			.toList();

		// JOIN FETCH로 Category, Tag, Contributor를 한 번에 조회
		List<BookCategory> bookCategories = bookCategoryRepository.findByBookIdsWithCategory(bookIds);
		List<BookTag> bookTags = bookTagRepository.findByBookIdsWithTag(bookIds);
		List<BookContributor> bookContributors = bookContributorRepository.findByBookIdsWithContributor(bookIds);

		// BookId별로 그룹화
		java.util.Map<Long, List<Category>> categoryMap = bookCategories.stream()
			.collect(java.util.stream.Collectors.groupingBy(
				bc -> bc.getBook().getBookId(),
				java.util.stream.Collectors.mapping(BookCategory::getCategory, java.util.stream.Collectors.toList())
			));
		java.util.Map<Long, List<Tag>> tagMap = bookTags.stream()
			.collect(java.util.stream.Collectors.groupingBy(
				bt -> bt.getBook().getBookId(),
				java.util.stream.Collectors.mapping(BookTag::getTag, java.util.stream.Collectors.toList())
			));
		java.util.Map<Long, List<Contributor>> contributorMap = bookContributors.stream()
			.collect(java.util.stream.Collectors.groupingBy(
				bc -> bc.getBook().getBookId(),
				java.util.stream.Collectors.mapping(BookContributor::getContributor,
					java.util.stream.Collectors.toList())
			));

		// @BatchSize가 BookImage를 자동으로 배치 로딩
		List<BookResponse> bookResponseList = bookList.stream()
			.map(book -> {
				List<Category> categories = categoryMap.getOrDefault(book.getBookId(), new ArrayList<>());
				List<Tag> tags = tagMap.getOrDefault(book.getBookId(), new ArrayList<>());
				List<Contributor> contributors = contributorMap.getOrDefault(book.getBookId(), new ArrayList<>());
				return toResponse(book, categories, tags, contributors);
			})
			.toList();

		log.info("도서 조회가 완료되었습니다. (페이지: {}, 크기: {})", pageable.getPageNumber(), pageable.getPageSize());
		return new PageImpl<>(bookResponseList, pageable, bookPage.getTotalElements());
	}

	@Override
	public List<BookResponse> getBooksByIds(List<Long> bookIds) {
		log.info("요청된 도서 ID 목록: {}", bookIds);

		List<Book> bookList = bookRepository.findAllById(bookIds);
		if (bookList.isEmpty()) {
			log.info("요청된 ID에 해당하는 도서가 존재하지 않습니다.");
			return new ArrayList<>();
		}

		List<BookResponse> bookResponseList = new ArrayList<>();
		for (Book book : bookList) {
			BookResponse response = toResponse(book);
			bookResponseList.add(response);
		}

		log.info("도서 다중 조회 완료 ({}건)", bookResponseList.size());
		return bookResponseList;
	}

	@Override
	public Book getBookWithPublisher(Long bookId) {
		return bookRepository.queryBookWithPublisherById(bookId);
	}

	@Override
	public BookReview getBookReview(Long bookId) {
		return bookRepository.queryBookReview(bookId);
	}

	@Override
	public BookOrderRequest getBookOrder(CartOrderRequest cartOrderRequest) {

		Set<Long> bookIds = cartOrderRequest.cartOrderList().keySet();

		List<Book> books = bookRepository.findAllByWithContributors(bookIds);

		List<BookInfoRequest> bookList = new ArrayList<>();

		for (Book book : books) {
			String firstImg = "https://image.yes24.com/momo/Noimg_L.jpg"; // 기본 이미지로 초기화

			if (book.getBookImageUrls() != null && !book.getBookImageUrls().isEmpty()) {

				BookImage firstImage = book.getBookImageUrls().getFirst();

				if (firstImage != null && firstImage.getBookImageUrl() != null) {
					firstImg = firstImage.getBookImageUrl();
				}
			}
			bookList.add(new BookInfoRequest(
				book.getBookId(),
				book.getBookName(),
				firstImg,
				book.isPack(),
				book.getRegularPrice(),
				book.getSalePrice(),
				new AllPublishersInfoResponse(book.getPublisher()),
				cartOrderRequest.cartOrderList().get(book.getBookId()), // 수량을 가져옴
				book.getBookContributors().stream().map(
					bc -> new AllContributorResponse(bc.getContributor())
				).toList(),
				null
			));
		}

		return new BookOrderRequest(bookList);
	}

	private BookResponse toResponse(Book book, List<Category> categories, List<Tag> tags,
		List<Contributor> contributors) {
		List<CategoryLeafResponse> categoryResponses = categories.stream()
			.map(category -> new CategoryLeafResponse(
				category.getCategoryId(),
				category.getCategoryName(),
				category.getPathName()
			))
			.toList();

		List<AllTagsInfoResponse> tagResponses = tags.stream().map(AllTagsInfoResponse::new).toList();

		List<AllContributorResponse> contributorResponses = contributors.stream()
			.map(AllContributorResponse::new)
			.toList();

		Publisher publisher = book.getPublisher();

		AllPublishersInfoResponse publisherResponse = new AllPublishersInfoResponse(publisher.getPublisherId(),
			publisher.getPublisherName());

		List<GetUrlResponse> imageResponses = bookImageService.getImageUrlsById(book.getBookId()).stream()
			.map(projection -> new GetUrlResponse(projection.getUrlId(), projection.getImageUrl()))
			.toList();

		return new BookResponse(
			book.getBookId(),
			book.getBookName(),
			book.getToc(),
			book.getDescription(),
			book.getRegularPrice(),
			book.getSalePrice(),
			book.getIsbn(),
			book.getPublishDate(),
			book.isPack(),
			book.getBookStatus(),
			book.getStock(),
			publisherResponse,
			book.isDeleted(),
			categoryResponses,
			tagResponses,
			contributorResponses,
			imageResponses
		);
	}

	private BookResponse toResponse(Book book) {
		List<Category> categories = bookCategoryService.getCategoriesByBookId(book.getBookId());
		List<Tag> tags = bookTagService.getTagsByBookId(book.getBookId());
		List<Contributor> contributors = bookContributorService.getContributorsByBookId(book.getBookId());
		return toResponse(book, categories, tags, contributors);
	}
}