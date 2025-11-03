package com.nhnacademy.byeol23backend.bookset.book.service.impl;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.nhnacademy.byeol23backend.bookset.book.domain.Book;
import com.nhnacademy.byeol23backend.bookset.book.dto.BookCreateRequest;
import com.nhnacademy.byeol23backend.bookset.book.dto.BookResponse;
import com.nhnacademy.byeol23backend.bookset.book.dto.BookUpdateRequest;
import com.nhnacademy.byeol23backend.bookset.book.exception.BookNotFoundException;
import com.nhnacademy.byeol23backend.bookset.book.exception.ISBNAlreadyExistException;
import com.nhnacademy.byeol23backend.bookset.book.repository.BookRepository;
import com.nhnacademy.byeol23backend.bookset.book.service.BookService;
import com.nhnacademy.byeol23backend.bookset.publisher.domain.Publisher;
import com.nhnacademy.byeol23backend.bookset.publisher.exception.PublisherNotFoundException;
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
	@Transactional
	public BookResponse updateBook(Long bookId, BookUpdateRequest updateRequest) {
		Book book = bookRepository.findById(bookId)
			.orElseThrow(() -> new BookNotFoundException("존재하지 않는 도서입니다: " + bookId));

		Publisher publisher = publisherRepository.findById(updateRequest.publisherId())
			.orElseThrow(() -> new PublisherNotFoundException("존재하지 않는 출판사 ID입니다: " + updateRequest.publisherId()));

		book.updateBook(updateRequest, publisher);
		log.info("도서 정보가 수정되었습니다. ID: {}", book.getBookId());

		return toResponse(book);
	}

	@Override
	@Transactional
	public void deleteBook(Long bookId) {
		Book book = bookRepository.findById(bookId)
			.orElseThrow(() -> new BookNotFoundException("존재하지 않는 도서입니다: " + bookId));
		bookRepository.delete(book);
		log.info("도서가 삭제 처리되었습니다. ID: {}", bookId);
	}

	@Override
	@Transactional
	public List<BookResponse> getBooks(Pageable pageable) {
		return bookRepository.findAll().stream()
			.map(this::toResponse)
			.collect(Collectors.toList());
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
