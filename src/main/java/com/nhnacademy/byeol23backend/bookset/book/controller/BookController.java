package com.nhnacademy.byeol23backend.bookset.book.controller;

import java.util.List;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.nhnacademy.byeol23backend.bookset.book.annotation.ViewerId;
import com.nhnacademy.byeol23backend.bookset.book.dto.BookCreateRequest;
import com.nhnacademy.byeol23backend.bookset.book.dto.BookResponse;
import com.nhnacademy.byeol23backend.bookset.book.dto.BookStockResponse;
import com.nhnacademy.byeol23backend.bookset.book.dto.BookStockUpdateRequest;
import com.nhnacademy.byeol23backend.bookset.book.dto.BookUpdateRequest;
import com.nhnacademy.byeol23backend.bookset.book.service.BookService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/books")
@RequiredArgsConstructor
@Validated
public class BookController {

	private final BookService bookService;

	@PostMapping
	public ResponseEntity<BookResponse> createBook(@Valid @RequestBody BookCreateRequest createRequest) {
		BookResponse response = bookService.createBook(createRequest);
		return ResponseEntity.status(HttpStatus.CREATED).body(response);
	}

	@GetMapping("/{bookId}")
	public ResponseEntity<BookResponse> getBook(@PathVariable("bookId") Long bookId, @ViewerId String viewerId) {
		BookResponse response = bookService.getBookAndIncreaseViewCount(bookId, viewerId);
		return ResponseEntity.ok(response);
	}

	@PutMapping("/{book-id}")
	public ResponseEntity<BookResponse> updateBook(@PathVariable("book-id") Long bookId,
		@Valid @RequestBody BookUpdateRequest updateRequest) {
		BookResponse response = bookService.updateBook(bookId, updateRequest);
		return ResponseEntity.ok(response);
	}

	@DeleteMapping("/{book-id}")
	public ResponseEntity<Void> deleteBook(@PathVariable("book-id") Long bookId) {
		bookService.deleteBook(bookId);
		return ResponseEntity.noContent().build();
	}

	@GetMapping
	public ResponseEntity<List<BookResponse>> getBooks(@RequestParam(defaultValue = "0") int pageNo,
		@RequestParam(defaultValue = "10") int pageSize
	) {
		Pageable pageable = PageRequest.of(pageNo, pageSize);
		List<BookResponse> books = bookService.getBooks(pageable);
		return ResponseEntity.ok(books);
	}

	@GetMapping("/{book-id}/stock")
	public ResponseEntity<BookStockResponse> getBookStock(@PathVariable("book-id") Long bookId) {
		return ResponseEntity.ok(bookService.getBookStock(bookId));
	}

	@PostMapping("/{book-id}/stock")
	public ResponseEntity<Void> updateBookStock(@PathVariable("book-id") Long bookId,
		@RequestBody BookStockUpdateRequest request) {
		bookService.updateBookStock(bookId, request);
		return ResponseEntity.noContent().build();
	}
}
