package com.nhnacademy.byeol23backend.bookset.book.service.impl;

import com.nhnacademy.byeol23backend.bookset.book.event.ViewCountIncreaseEvent;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.nhnacademy.byeol23backend.bookset.book.domain.Book;
import com.nhnacademy.byeol23backend.bookset.book.dto.BookCreateRequest;
import com.nhnacademy.byeol23backend.bookset.book.dto.BookResponse;
import com.nhnacademy.byeol23backend.bookset.book.dto.BookUpdateRequest;
import com.nhnacademy.byeol23backend.bookset.book.exception.BookNotFoundException;
import com.nhnacademy.byeol23backend.bookset.book.repository.BookRepository;
import com.nhnacademy.byeol23backend.bookset.book.service.BookService;
import com.nhnacademy.byeol23backend.bookset.publisher.domain.Publisher;
import com.nhnacademy.byeol23backend.bookset.publisher.repository.PublisherRepository;

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

	@Override
	@Transactional
	public BookResponse createBook(BookCreateRequest createRequest) {
		if (bookRepository.existsByIsbn(createRequest.isbn())) {
			throw new IllegalArgumentException("이미 존재하는 ISBN입니다: " + createRequest.isbn());
		}
		//현재 publisher의 데이터는 깡통 데이터를 쓰고있습니다.
		// 존재하지 않는 출판사 오류도 구현 필요합니다.
		Publisher publisher = publisherRepository.findById(createRequest.publisherId())
			.orElseThrow(() -> new IllegalArgumentException("존재하지 않는 출판사 ID입니다: " + createRequest.publisherId()));

		Book book = new Book();
		book.setBookName(createRequest.bookName());
		book.setToc(createRequest.toc());
		book.setDescription(createRequest.description());
		book.setRegularPrice(createRequest.regularPrice());
		book.setSalePrice(createRequest.salePrice());
		book.setIsbn(createRequest.isbn());
		book.setPublishDate(createRequest.publishDate());
		book.setPack(createRequest.isPack());
		book.setBookStatus(createRequest.bookStatus());
		book.setStock(createRequest.stock());
		book.setPublisher(publisher);
		book.setDeleted(createRequest.isDeleted());
		Book savedBook = bookRepository.save(book);
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

		if (bookRepository.existsByIsbnAndBookIdNot(updateRequest.isbn(), bookId)) {
			throw new IllegalArgumentException("다른 도서에서 이미 사용 중인 ISBN입니다: " + updateRequest.isbn());
		}

		Publisher publisher = publisherRepository.findById(updateRequest.publisherId())
			.orElseThrow(() -> new IllegalArgumentException("존재하지 않는 출판사 ID입니다: " + updateRequest.publisherId()));

		book.setBookName(updateRequest.bookName());
		book.setToc(updateRequest.toc());
		book.setRegularPrice(updateRequest.regularPrice());
		book.setSalePrice(updateRequest.salePrice());
		book.setIsbn(updateRequest.isbn());
		book.setPublishDate(updateRequest.publishDate());
		book.setPack(updateRequest.isPack());
		book.setBookStatus(updateRequest.bookStatus());
		book.setStock(updateRequest.stock());
		book.setPublisher(publisher);
		book.setDeleted(updateRequest.isDeleted()); // 수정에서는 삭제할 필요가 없음
		// Book updatedBook = bookRepository.save(book);
		// log.info("도서 정보가 수정되었습니다. ID: {}", updatedBook.getBookId());

		return toResponse(book);
	}

	private BookResponse toResponse(Book book) {
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
			book.getPublisher().getPublisherId(),
			book.isDeleted()
		);
	}
}
