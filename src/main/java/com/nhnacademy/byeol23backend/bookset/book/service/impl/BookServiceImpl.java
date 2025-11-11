package com.nhnacademy.byeol23backend.bookset.book.service.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.nhnacademy.byeol23backend.bookset.book.domain.Book;
import com.nhnacademy.byeol23backend.bookset.book.dto.BookCreateRequest;
import com.nhnacademy.byeol23backend.bookset.book.dto.BookResponse;
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
import com.nhnacademy.byeol23backend.bookset.booktag.domain.BookTag;
import com.nhnacademy.byeol23backend.bookset.booktag.repository.BookTagRepository;
import com.nhnacademy.byeol23backend.bookset.booktag.service.BookTagService;
import com.nhnacademy.byeol23backend.bookset.category.domain.Category;
import com.nhnacademy.byeol23backend.bookset.category.dto.CategoryLeafResponse;
import com.nhnacademy.byeol23backend.bookset.contributor.domain.Contributor;
import com.nhnacademy.byeol23backend.bookset.contributor.domain.dto.AllContributorResponse;
import com.nhnacademy.byeol23backend.bookset.publisher.domain.Publisher;
import com.nhnacademy.byeol23backend.bookset.publisher.domain.dto.AllPublishersInfoResponse;
import com.nhnacademy.byeol23backend.bookset.publisher.exception.PublisherNotFoundException;
import com.nhnacademy.byeol23backend.bookset.publisher.repository.PublisherRepository;
import com.nhnacademy.byeol23backend.bookset.tag.domain.Tag;
import com.nhnacademy.byeol23backend.bookset.tag.domain.dto.AllTagsInfoResponse;

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

		return toResponse(savedBook);
	}

	@Override
	public BookResponse getBook(Long bookId) {
		Book book = bookRepository.findById(bookId)
			.orElseThrow(() -> new BookNotFoundException("존재하지 않는 도서입니다: " + bookId));
		return toResponse(book);
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

		book.updateBook(updateRequest, publisher);
		bookCategoryService.updateBookCategories(book, updateRequest.categoryIds());
		bookTagService.updateBookTags(book, updateRequest.tagIds());
		bookContributorService.updateBookContributors(book, updateRequest.contributorIds());
		log.info("도서 정보가 수정되었습니다. ID: {}", book.getBookId());

		return toResponse(book);
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
	}

	@Override
	public List<BookResponse> getBooks(Pageable pageable) {
		List<Book> bookList = bookRepository.findAll();
		if (bookList.isEmpty()) {
			return new ArrayList<>();
		}

		List<Long> bookIdList = new ArrayList<>();
		for (Book bookItem : bookList) {
			Long bookId = bookItem.getBookId();
			bookIdList.add(bookId);
		}
		List<BookCategory> bookCategoryList = bookCategoryRepository.findByBookIdsWithCategory(bookIdList);
		Map<Long, List<Category>> bookIdToCategoryListMap = new HashMap<>();
		List<BookTag> bookTagList = bookTagRepository.findByBookIdsWithTag(bookIdList);
		Map<Long, List<Tag>> bookIdToTagListMap = new HashMap<>();
		List<BookContributor> bookContributorList = bookContributorRepository.findByBookIdsWithContributor(bookIdList);
		Map<Long, List<Contributor>> bookIdToContributorListMap = new HashMap<>();

		for (BookCategory bookCategoryItem : bookCategoryList) {
			Book bookFromCategory = bookCategoryItem.getBook();
			Long bookIdFromCategory = bookFromCategory.getBookId();
			Category categoryFromBookCategory = bookCategoryItem.getCategory();
			if (!bookIdToCategoryListMap.containsKey(bookIdFromCategory)) {
				List<Category> newCategoryList = new ArrayList<>();
				bookIdToCategoryListMap.put(bookIdFromCategory, newCategoryList);
			}
			List<Category> existingCategoryList = bookIdToCategoryListMap.get(bookIdFromCategory);
			existingCategoryList.add(categoryFromBookCategory);
		}
		log.info("도서별 카테고리 정보 조회 완료");

		for (BookTag bookTagItem : bookTagList) {
			Book bookFromTag = bookTagItem.getBook();
			Long bookIdFromTag = bookFromTag.getBookId();
			Tag tagFromBookTag = bookTagItem.getTag();
			if (!bookIdToTagListMap.containsKey(bookIdFromTag)) {
				List<Tag> newTagList = new ArrayList<>();
				bookIdToTagListMap.put(bookIdFromTag, newTagList);
			}
			List<Tag> existingTagList = bookIdToTagListMap.get(bookIdFromTag);
			existingTagList.add(tagFromBookTag);
		}
		log.info("도서별 태그 정보 조회 완료");

		for (BookContributor bookContributorItem : bookContributorList) {
			Book bookFromContributor = bookContributorItem.getBook();
			Long bookIdFromContributor = bookFromContributor.getBookId();
			Contributor contributorFromBookContributor = bookContributorItem.getContributor();
			if (!bookIdToContributorListMap.containsKey(bookIdFromContributor)) {
				List<Contributor> newContributorList = new ArrayList<>();
				bookIdToContributorListMap.put(bookIdFromContributor, newContributorList);
			}
			List<Contributor> existingContributorList = bookIdToContributorListMap.get(bookIdFromContributor);
			existingContributorList.add(contributorFromBookContributor);
		}
		log.info("도서별 기여자 정보 조회 완료");

		List<BookResponse> bookResponseList = new ArrayList<>();

		for (Book bookItem : bookList) {
			Long currentBookId = bookItem.getBookId();
			List<Category> categoryListForBook = bookIdToCategoryListMap.getOrDefault(currentBookId, new ArrayList<>());
			List<Tag> tagListForBook = bookIdToTagListMap.getOrDefault(currentBookId, new ArrayList<>());
			List<Contributor> contributorListForBook = bookIdToContributorListMap.getOrDefault(currentBookId,
				new ArrayList<>());
			BookResponse bookResponse = toResponse(bookItem, categoryListForBook, tagListForBook,
				contributorListForBook);
			bookResponseList.add(bookResponse);
		}
		log.info("도서 조회가 완료되었습니다.");
		return bookResponseList;
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

		List<AllContributorResponse> contributorResonses = contributors.stream()
			.map(AllContributorResponse::new)
			.toList();

		Publisher publisher = publisherRepository.findById(book.getPublisher().getPublisherId())
			.orElseThrow(() -> new PublisherNotFoundException(
				"해당 아이디의 출판사를 찾을 수 없습니다.: " + book.getPublisher().getPublisherId()));

		AllPublishersInfoResponse publisherResponse = new AllPublishersInfoResponse(publisher.getPublisherId(),
			publisher.getPublisherName());

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
			contributorResonses
		);
	}

	private BookResponse toResponse(Book book) {
		List<Category> categories = bookCategoryService.getCategoriesByBookId(book.getBookId());
		List<Tag> tags = bookTagService.getTagsByBookId(book.getBookId());
		List<Contributor> contributors = bookContributorService.getContributorsByBookId(book.getBookId());
		return toResponse(book, categories, tags, contributors);
	}
}