package com.nhnacademy.byeol23backend.bookset.book.controller;

import java.util.List;

import org.springframework.data.domain.Page;
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

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping("/api/books")
@RequiredArgsConstructor
@Validated
public class BookController {

	private final BookService bookService;

	@Operation(summary = "도서 추가", description = "새로운 도서를 추가합니다.")
	@ApiResponses({
		@ApiResponse(responseCode = "201", description = "도서 추가 성공"),
		@ApiResponse(responseCode = "400", description = "잘못된 요청 데이터")
	})
	@PostMapping
	public ResponseEntity<BookResponse> createBook(@Valid @RequestBody BookCreateRequest createRequest) {
		BookResponse response = bookService.createBook(createRequest);
		return ResponseEntity.status(HttpStatus.CREATED).body(response);
	}

	@Operation(summary = "도서 상세 조회", description = "도서 ID로 도서 상세 정보를 조회하고 조회수를 증가시킵니다.")
	@ApiResponses({
		@ApiResponse(responseCode = "200", description = "도서 조회 성공"),
		@ApiResponse(responseCode = "400", description = "잘못된 요청 데이터")
	})
	@Parameter(name = "bookId", description = "도서 ID", required = true, example = "1")
	@GetMapping("/{bookId}")
	public ResponseEntity<BookResponse> getBook(@PathVariable("bookId") Long bookId, @ViewerId String viewerId) {
		log.info("viewerId: {}", viewerId);
		BookResponse response = bookService.getBookAndIncreaseViewCount(bookId, viewerId);
		return ResponseEntity.ok(response);
	}

	@Operation(summary = "도서 수정", description = "요청 바디로 들어온 값으로 도서 정보를 수정합니다.")
	@ApiResponses({
		@ApiResponse(responseCode = "200", description = "도서 수정 성공"),
		@ApiResponse(responseCode = "400", description = "도서 수정 실패 또는 잘못된 요청")
	})
	@Parameter(name = "book-id", description = "도서 ID", required = true, example = "1")
	@PutMapping("/{book-id}")
	public ResponseEntity<BookResponse> updateBook(@PathVariable("book-id") Long bookId,
		@Valid @RequestBody BookUpdateRequest updateRequest) {
		BookResponse response = bookService.updateBook(bookId, updateRequest);
		return ResponseEntity.ok(response);
	}

	@Operation(summary = "도서 삭제", description = "도서를 삭제합니다 (Soft Delete).")
	@ApiResponses({
		@ApiResponse(responseCode = "200", description = "도서 soft delete 성공"),
		@ApiResponse(responseCode = "400", description = "삭제 실패 또는 잘못된 요청")
	})
	@Parameter(name = "book-id", description = "도서 ID", required = true, example = "1")
	@DeleteMapping("/{book-id}")
	public ResponseEntity<Void> deleteBook(@PathVariable("book-id") Long bookId) {
		bookService.deleteBook(bookId);
		return ResponseEntity.noContent().build();
	}

	@Operation(summary = "도서 목록 조회", description = "페이징된 도서 목록을 조회합니다.")
	@ApiResponses({
		@ApiResponse(responseCode = "200", description = "도서 목록 조회 성공"),
		@ApiResponse(responseCode = "400", description = "잘못된 요청 데이터")
	})
	@GetMapping
	public ResponseEntity<Page<BookResponse>> getBooks(@RequestParam(value = "page", defaultValue = "0") int page,
		@RequestParam(value = "size", defaultValue = "20") int size
	) {
		Pageable pageable = PageRequest.of(page, size);
		Page<BookResponse> books = bookService.getBooks(pageable);
		return ResponseEntity.ok(books);
	}

	@Operation(summary = "도서 목록 조회 (ID 리스트)", description = "여러 도서 ID로 도서 목록을 조회합니다.")
	@ApiResponses({
		@ApiResponse(responseCode = "200", description = "도서 목록 조회 성공"),
		@ApiResponse(responseCode = "400", description = "잘못된 요청 데이터")
	})
	@Parameter(name = "ids", description = "도서 ID 리스트 (쉼표로 구분)", required = true, example = "1,2,3")
	@GetMapping("/list")
	public ResponseEntity<List<BookResponse>> getBooksByIds(@RequestParam("ids") List<Long> bookIds) {
		List<BookResponse> books = bookService.getBooksByIds(bookIds);
		return ResponseEntity.ok(books);
	}

	@Operation(summary = "도서 재고 조회", description = "도서 재고만 따로 조회합니다.")
	@ApiResponses({
		@ApiResponse(responseCode = "200", description = "도서 재고 조회 성공"),
		@ApiResponse(responseCode = "400", description = "잘못된 요청 데이터")
	})
	@Parameter(name = "book-id", description = "도서 ID", required = true, example = "1")
	@GetMapping("/{book-id}/stock")
	public ResponseEntity<BookStockResponse> getBookStock(@PathVariable("book-id") Long bookId) {
		return ResponseEntity.ok(bookService.getBookStock(bookId));
	}

	@Operation(summary = "도서 재고 수정", description = "도서의 재고만 따로 수정합니다.")
	@ApiResponses({
		@ApiResponse(responseCode = "200", description = "도서 재고 수정 성공"),
		@ApiResponse(responseCode = "400", description = "잘못된 요청 데이터")
	})
	@Parameter(name = "book-id", description = "도서 ID", required = true, example = "1")
	@PutMapping("/{book-id}/stock")
	public ResponseEntity<Void> updateBookStock(@PathVariable("book-id") Long bookId,
		@RequestBody BookStockUpdateRequest request) {
		bookService.updateBookStock(bookId, request);
		return ResponseEntity.noContent().build();
	}
}
