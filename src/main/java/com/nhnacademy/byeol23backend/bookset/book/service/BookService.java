package com.nhnacademy.byeol23backend.bookset.book.service;

import com.nhnacademy.byeol23backend.bookset.book.domain.Book;
import com.nhnacademy.byeol23backend.bookset.book.dto.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

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

    BookReview getBookReview(Long bookId);
}
