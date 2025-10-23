package com.nhnacademy.byeol23backend.bookset.book.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.nhnacademy.byeol23backend.bookset.book.dto.BookCreateRequest;
import com.nhnacademy.byeol23backend.bookset.book.dto.BookResponse;
import com.nhnacademy.byeol23backend.bookset.book.dto.BookUpdateRequest;
import com.nhnacademy.byeol23backend.bookset.book.service.BookService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/books")
@RequiredArgsConstructor
public class BookController {

	private final BookService bookService;

	@PostMapping
	public ResponseEntity<BookResponse> createBook(@Valid @RequestBody BookCreateRequest createRequest) {
		BookResponse response = bookService.createBook(createRequest);
		return ResponseEntity.status(HttpStatus.CREATED).body(response);
	}

	@GetMapping("/{bookId}")
	public ResponseEntity<BookResponse> getBook(@PathVariable("bookId") Long bookId) {
		BookResponse response = bookService.getBook(bookId);
		return ResponseEntity.ok(response);
	}

	@PutMapping("/{bookId}")
	public ResponseEntity<BookResponse> updateBook(@PathVariable("bookId") Long bookId,
		@Valid @RequestBody BookUpdateRequest updateRequest) {
		BookResponse response = bookService.updateBook(bookId, updateRequest);
		return ResponseEntity.ok(response);
	}
}
