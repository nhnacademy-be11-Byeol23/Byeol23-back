package com.nhnacademy.byeol23backend.bookset.book.service;

import com.nhnacademy.byeol23backend.bookset.book.dto.BookCreateRequest;
import com.nhnacademy.byeol23backend.bookset.book.dto.BookResponse;
import com.nhnacademy.byeol23backend.bookset.book.dto.BookUpdateRequest;

public interface BookService {
	BookResponse createBook(BookCreateRequest createRequest);

	BookResponse getBook(Long bookId);

    BookResponse getBookAndIncreaseViewCount(Long bookId, String viewerId);

	BookResponse updateBook(Long bookId, BookUpdateRequest updateRequest);
}
