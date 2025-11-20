package com.nhnacademy.byeol23backend.bookset.book.service;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.nhnacademy.byeol23backend.bookset.book.domain.Book;
import com.nhnacademy.byeol23backend.bookset.book.dto.BookCreateRequest;
import com.nhnacademy.byeol23backend.bookset.book.dto.BookResponse;
import com.nhnacademy.byeol23backend.bookset.book.dto.BookStockResponse;
import com.nhnacademy.byeol23backend.bookset.book.dto.BookStockUpdateRequest;
import com.nhnacademy.byeol23backend.bookset.book.dto.BookUpdateRequest;

public interface BookService {
	BookResponse createBook(BookCreateRequest createRequest);

	BookResponse getBook(Long bookId);

	BookResponse getBookAndIncreaseViewCount(Long bookId, String viewerId);

	BookResponse updateBook(Long bookId, BookUpdateRequest updateRequest);

	void deleteBook(Long bookId);

	Page<BookResponse> getBooks(Pageable pageable);

	List<BookResponse> getBooksByIds(List<Long> bookIds);

	Book getBookWithPublisher(Long bookId);

	void updateBookStock(Long bookId, BookStockUpdateRequest request);

	BookStockResponse getBookStock(Long bookId);
}
