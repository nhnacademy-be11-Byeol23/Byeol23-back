package com.nhnacademy.byeol23backend.bookset.book.service;

import com.nhnacademy.byeol23backend.bookset.book.dto.BookRequestDto;
import com.nhnacademy.byeol23backend.bookset.book.dto.BookResponseDto;

public interface BookService {

	BookResponseDto createBook(BookRequestDto requestDto);

	BookResponseDto findById(Long bookId);

	BookResponseDto updateBook(Long bookId, BookRequestDto requestDto);
}