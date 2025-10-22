package com.nhnacademy.byeol23backend.bookset.book.controller;

import java.util.HashMap;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.nhnacademy.byeol23backend.bookset.book.dto.BookRequestDto;
import com.nhnacademy.byeol23backend.bookset.book.dto.BookResponseDto;
import com.nhnacademy.byeol23backend.bookset.book.service.BookService;

@RestController
@RequestMapping("/api/books")
class BookController {

	private final BookService bookService;

	public BookController(BookService bookService) {
		this.bookService = bookService;
	}

	@PostMapping
	public ResponseEntity<BookResponseDto> createBook(@RequestBody BookRequestDto requestDto) {
		BookResponseDto response = bookService.createBook(requestDto);
		return ResponseEntity.status(HttpStatus.CREATED).body(response);
	}

	@RequestMapping(path = "{id}")
	public ResponseEntity<BookResponseDto> getBook(@PathVariable("id") Long id) {
		BookResponseDto response = bookService.findById(id);
		return ResponseEntity.ok(response);
	}

	@PutMapping(path = "{id}")
	public ResponseEntity<BookResponseDto> updateBook(@PathVariable("id") Long id,
		@RequestBody BookRequestDto requestDto) {
		BookResponseDto response = bookService.updateBook(id, requestDto);
		return ResponseEntity.ok(response);
	}

	@ExceptionHandler(IllegalArgumentException.class)
	public ResponseEntity<Map<String, String>> handleNotFound(IllegalArgumentException ex) {
		Map<String, String> body = new HashMap<>();
		body.put("error", ex.getMessage());
		return ResponseEntity.status(HttpStatus.NOT_FOUND).body(body);
	}
}
