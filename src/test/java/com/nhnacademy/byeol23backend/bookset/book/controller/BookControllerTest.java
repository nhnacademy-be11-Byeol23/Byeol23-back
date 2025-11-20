package com.nhnacademy.byeol23backend.bookset.book.controller;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import com.nhnacademy.byeol23backend.bookset.book.domain.BookStatus;
import com.nhnacademy.byeol23backend.bookset.book.dto.BookCreateRequest;
import com.nhnacademy.byeol23backend.bookset.book.dto.BookResponse;
import com.nhnacademy.byeol23backend.bookset.book.dto.BookStockResponse;
import com.nhnacademy.byeol23backend.bookset.book.dto.BookStockUpdateRequest;
import com.nhnacademy.byeol23backend.bookset.book.dto.BookUpdateRequest;
import com.nhnacademy.byeol23backend.bookset.book.exception.BookNotFoundException;
import com.nhnacademy.byeol23backend.bookset.book.exception.ISBNAlreadyExistException;
import com.nhnacademy.byeol23backend.bookset.book.service.BookService;
import com.nhnacademy.byeol23backend.bookset.category.dto.CategoryLeafResponse;
import com.nhnacademy.byeol23backend.bookset.contributor.domain.dto.AllContributorResponse;
import com.nhnacademy.byeol23backend.bookset.publisher.domain.dto.AllPublishersInfoResponse;
import com.nhnacademy.byeol23backend.bookset.tag.domain.dto.AllTagsInfoResponse;

@ExtendWith(MockitoExtension.class)
class BookControllerTest {

	@Mock
	private BookService bookService;

	@InjectMocks
	private BookController bookController;

	@Test
	@DisplayName("도서 생성 성공 - POST /api/books")
	void createBook_Success() {
		// given
		List<Long> categoryIds = List.of(1L, 2L);
		List<Long> tagIds = List.of(1L, 2L);
		List<Long> contributorIds = List.of(1L);
		BookCreateRequest createRequest = new BookCreateRequest(
			"８월에 만나요",
			"1장, 2장, 3장, 4장, 5장, 6장",
			"결혼한 지 스물일곱 해가 된 평범한 주부 아나 막달레나 바흐는 ......",
			new BigDecimal(16000),
			new BigDecimal(15990),
			"1234567890123",
			LocalDate.of(2024, 5, 15),
			true,
			BookStatus.SALE,
			12,
			1L,
			categoryIds,
			tagIds,
			contributorIds,
			List.of(),
			null
		);

		BookResponse response = new BookResponse(
			1L,
			"８월에 만나요",
			"1장, 2장, 3장, 4장, 5장, 6장",
			"결혼한 지 스물일곱 해가 된 평범한 주부 아나 막달레나 바흐는 ......",
			new BigDecimal(16000),
			new BigDecimal(15990),
			"1234567890123",
			LocalDate.of(2024, 5, 15),
			true,
			BookStatus.SALE,
			12,
			new AllPublishersInfoResponse(1L, "출판사1"),
			false,
			List.of(
				new CategoryLeafResponse(1L, "국내도서", "국내도서"),
				new CategoryLeafResponse(2L, "소설", "국내도서/소설")
			),
			List.of(
				new AllTagsInfoResponse(1L, "태그 이름")
			),
			List.of(
				new AllContributorResponse(1L, "작가이름", "저자")
			),
			List.of()
		);

		given(bookService.createBook(any(BookCreateRequest.class))).willReturn(response);

		// when
		ResponseEntity<BookResponse> result = bookController.createBook(createRequest);

		// then
		assertThat(result.getStatusCode()).isEqualTo(HttpStatus.CREATED);
		assertThat(result.getBody()).isNotNull();
		BookResponse body = result.getBody();
		assertThat(body.bookId()).isEqualTo(1L);
		assertThat(body.bookName()).isEqualTo("８월에 만나요");
		assertThat(body.isbn()).isEqualTo("1234567890123");
		assertThat(body.regularPrice()).isEqualByComparingTo(new BigDecimal(16000));
		assertThat(body.salePrice()).isEqualByComparingTo(new BigDecimal(15990));
		assertThat(body.categories()).hasSize(2);
		assertThat(body.categories().get(0).id()).isEqualTo(1L);
		assertThat(body.categories().get(1).id()).isEqualTo(2L);

		verify(bookService, times(1)).createBook(createRequest);
	}

	@Test
	@DisplayName("도서 생성 실패 - 중복된 ISBN")
	void createBook_Fail_DuplicateISBN() {
		// given
		BookCreateRequest createRequest = new BookCreateRequest(
			"８월에 만나요",
			"1장, 2장, 3장, 4장, 5장, 6장",
			"설명",
			new BigDecimal(16000),
			new BigDecimal(15990),
			"1234567890123",
			LocalDate.of(2024, 5, 15),
			true,
			BookStatus.SALE,
			12,
			1L,
			List.of(),
			List.of(),
			List.of(),
			List.of(),
			null
		);

		given(bookService.createBook(any(BookCreateRequest.class)))
			.willThrow(new ISBNAlreadyExistException("이미 존재하는 ISBN입니다: 1234567890123"));

		// when & then
		assertThatThrownBy(() -> bookController.createBook(createRequest))
			.isInstanceOf(ISBNAlreadyExistException.class)
			.hasMessageContaining("이미 존재하는 ISBN입니다");

		verify(bookService, times(1)).createBook(createRequest);
	}

	@Test
	@DisplayName("도서 조회 성공 - GET /api/books/{bookId}")
	void getBook_Success() {
		// given
		Long bookId = 1L;
		String viewerId = "guest:12345";
		BookResponse response = new BookResponse(
			bookId,
			"８월에 만나요",
			"1장, 2장, 3장, 4장, 5장, 6장",
			"설명",
			new BigDecimal(16000),
			new BigDecimal(15990),
			"1234567890123",
			LocalDate.of(2024, 5, 15),
			true,
			BookStatus.SALE,
			12,
			new AllPublishersInfoResponse(1L, "출판사 이름"),
			false,
			List.of(new CategoryLeafResponse(1L, "국내도서", "국내도서")),
			List.of(new AllTagsInfoResponse(1L, "태그 이름")),
			List.of(new AllContributorResponse(1L, "기여자 이름", "저자")),
			List.of()
		);

		given(bookService.getBookAndIncreaseViewCount(bookId, viewerId)).willReturn(response);

		// when
		ResponseEntity<BookResponse> result = bookController.getBook(bookId, viewerId);

		// then
		assertThat(result.getStatusCode()).isEqualTo(HttpStatus.OK);
		assertThat(result.getBody()).isNotNull();
		BookResponse body = result.getBody();
		assertThat(body.bookId()).isEqualTo(bookId);
		assertThat(body.bookName()).isEqualTo("８월에 만나요");
		assertThat(body.categories()).hasSize(1);
		assertThat(body.categories().get(0).id()).isEqualTo(1L);

		verify(bookService, times(1)).getBookAndIncreaseViewCount(bookId, viewerId);
	}

	@Test
	@DisplayName("도서 조회 실패 - 존재하지 않는 도서")
	void getBook_Fail_NotFound() {
		// given
		Long bookId = 999L;
		String viewerId = "guest:12345";
		given(bookService.getBookAndIncreaseViewCount(bookId, viewerId))
			.willThrow(new BookNotFoundException("존재하지 않는 도서입니다: " + bookId));

		// when & then
		assertThatThrownBy(() -> bookController.getBook(bookId, viewerId))
			.isInstanceOf(BookNotFoundException.class)
			.hasMessageContaining("존재하지 않는 도서입니다");

		verify(bookService, times(1)).getBookAndIncreaseViewCount(bookId, viewerId);
	}

	@Test
	@DisplayName("도서 수정 성공 - PUT /api/books/{book-id}")
	void updateBook_Success() {
		// given
		Long bookId = 1L;
		BookUpdateRequest updateRequest = new BookUpdateRequest(
			"수정된 도서명",
			"수정된 목차",
			"수정된 설명",
			new BigDecimal(20000),
			new BigDecimal(19000),
			LocalDate.of(2024, 6, 1),
			false,
			BookStatus.SOLDOUT,
			2L,
			List.of(1L, 2L),
			List.of(1L),
			List.of(1L),
			List.of()
		);

		BookResponse response = new BookResponse(
			bookId,
			"수정된 도서명",
			"수정된 목차",
			"수정된 설명",
			new BigDecimal(20000),
			new BigDecimal(19000),
			"1234567890123",
			LocalDate.of(2024, 6, 1),
			false,
			BookStatus.SOLDOUT,
			0,
			new AllPublishersInfoResponse(1L, "출판사"),
			false,
			List.of(
				new CategoryLeafResponse(1L, "국내도서", "국내도서"),
				new CategoryLeafResponse(2L, "소설", "국내도서/소설")
			),
			List.of(new AllTagsInfoResponse(1L, "태그"),
				new AllTagsInfoResponse(2L, "태그그")),
			List.of(new AllContributorResponse(1L, "저자명", "저자")),
			List.of()
		);

		given(bookService.updateBook(bookId, updateRequest)).willReturn(response);

		// when
		ResponseEntity<BookResponse> result = bookController.updateBook(bookId, updateRequest);

		// then
		assertThat(result.getStatusCode()).isEqualTo(HttpStatus.OK);
		assertThat(result.getBody()).isNotNull();
		BookResponse body = result.getBody();
		assertThat(body.bookId()).isEqualTo(bookId);
		assertThat(body.bookName()).isEqualTo("수정된 도서명");
		assertThat(body.regularPrice()).isEqualByComparingTo(new BigDecimal(20000));
		assertThat(body.salePrice()).isEqualByComparingTo(new BigDecimal(19000));
		assertThat(body.bookStatus()).isEqualTo(BookStatus.SOLDOUT);
		assertThat(body.stock()).isEqualTo(0);
		assertThat(body.categories()).hasSize(2);

		verify(bookService, times(1)).updateBook(bookId, updateRequest);
	}

	@Test
	@DisplayName("도서 수정 실패 - 존재하지 않는 도서")
	void updateBook_Fail_NotFound() {
		// given
		Long bookId = 999L;
		BookUpdateRequest updateRequest = new BookUpdateRequest(
			"수정된 도서명",
			"수정된 목차",
			"수정된 설명",
			new BigDecimal(20000),
			new BigDecimal(19000),
			LocalDate.of(2024, 6, 1),
			false,
			BookStatus.SALE,
			1L,
			List.of(),
			List.of(),
			List.of(),
			List.of()
		);

		given(bookService.updateBook(bookId, updateRequest))
			.willThrow(new BookNotFoundException("존재하지 않는 도서입니다: " + bookId));

		// when & then
		assertThatThrownBy(() -> bookController.updateBook(bookId, updateRequest))
			.isInstanceOf(BookNotFoundException.class)
			.hasMessageContaining("존재하지 않는 도서입니다");

		verify(bookService, times(1)).updateBook(bookId, updateRequest);
	}

	@Test
	@DisplayName("도서 삭제 성공 - DELETE /api/books/{book-id}")
	void deleteBook_Success() {
		// given
		Long bookId = 1L;
		willDoNothing().given(bookService).deleteBook(bookId);

		// when
		ResponseEntity<Void> result = bookController.deleteBook(bookId);

		// then
		assertThat(result.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
		assertThat(result.getBody()).isNull();

		verify(bookService, times(1)).deleteBook(bookId);
	}

	@Test
	@DisplayName("도서 삭제 실패 - 존재하지 않는 도서")
	void deleteBook_Fail_NotFound() {
		// given
		Long bookId = 999L;
		willThrow(new BookNotFoundException("존재하지 않는 도서입니다: " + bookId))
			.given(bookService).deleteBook(bookId);

		// when & then
		assertThatThrownBy(() -> bookController.deleteBook(bookId))
			.isInstanceOf(BookNotFoundException.class)
			.hasMessageContaining("존재하지 않는 도서입니다");

		verify(bookService, times(1)).deleteBook(bookId);
	}

	@Test
	@DisplayName("도서 목록 조회 성공 - GET /api/books")
	void getBooks_Success() {
		// given
		BookResponse book1 = new BookResponse(
			1L,
			"도서1",
			"목차1",
			"설명1",
			new BigDecimal(10000),
			new BigDecimal(9000),
			"1234567890123",
			LocalDate.of(2024, 5, 15),
			false,
			BookStatus.SALE,
			10,
			new AllPublishersInfoResponse(1L, "출판사"),
			false,
			List.of(new CategoryLeafResponse(1L, "국내도서", "국내도서")),
			List.of(new AllTagsInfoResponse(1L, "태그 이름")),
			List.of(new AllContributorResponse(1L, "기여자", "저자")),
			List.of()
		);

		BookResponse book2 = new BookResponse(
			2L,
			"도서2",
			"목차2",
			"설명2",
			new BigDecimal(20000),
			new BigDecimal(18000),
			"9876543210987",
			LocalDate.of(2024, 6, 1),
			false,
			BookStatus.SALE,
			5,
			new AllPublishersInfoResponse(1L, "출판사"),
			false,
			List.of(new CategoryLeafResponse(1L, "국내도서", "국내도서")),
			List.of(new AllTagsInfoResponse(1L, "태그 이름")),
			List.of(new AllContributorResponse(1L, "기여자", "저자")),
			List.of()
		);

		Pageable pageable = PageRequest.of(0, 20);
		Page<BookResponse> bookPage = new PageImpl<>(List.of(book1, book2), pageable, 2);
		given(bookService.getBooks(any(Pageable.class))).willReturn(bookPage);

		// when
		ResponseEntity<Page<BookResponse>> result = bookController.getBooks(0, 20);

		// then
		assertThat(result.getStatusCode()).isEqualTo(HttpStatus.OK);
		assertThat(result.getBody()).isNotNull();
		Page<BookResponse> body = result.getBody();
		assertThat(body.getContent()).hasSize(2);
		assertThat(body.getContent().get(0).bookId()).isEqualTo(1L);
		assertThat(body.getContent().get(0).bookName()).isEqualTo("도서1");
		assertThat(body.getContent().get(1).bookId()).isEqualTo(2L);
		assertThat(body.getContent().get(1).bookName()).isEqualTo("도서2");
		assertThat(body.getTotalElements()).isEqualTo(2);

		verify(bookService, times(1)).getBooks(any(Pageable.class));
	}

	@Test
	@DisplayName("도서 목록 조회 성공 - 빈 목록")
	void getBooks_Success_EmptyList() {
		// given
		Pageable pageable = PageRequest.of(0, 20);
		Page<BookResponse> emptyPage = Page.empty(pageable);
		given(bookService.getBooks(any(Pageable.class))).willReturn(emptyPage);

		// when
		ResponseEntity<Page<BookResponse>> result = bookController.getBooks(0, 20);

		// then
		assertThat(result.getStatusCode()).isEqualTo(HttpStatus.OK);
		assertThat(result.getBody()).isNotNull();
		assertThat(result.getBody().getContent()).isEmpty();

		verify(bookService, times(1)).getBooks(any(Pageable.class));
	}

	@Test
	@DisplayName("도서 ID 목록으로 조회 성공 - GET /api/books/list")
	void getBooksByIds_Success() {
		// given
		List<Long> bookIds = List.of(1L, 2L);
		BookResponse book1 = new BookResponse(
			1L,
			"도서1",
			null,
			null,
			new BigDecimal(10000),
			new BigDecimal(9000),
			"1234567890123",
			LocalDate.of(2024, 5, 15),
			false,
			BookStatus.SALE,
			10,
			new AllPublishersInfoResponse(1L, "출판사"),
			false,
			List.of(),
			List.of(),
			List.of(),
			List.of()
		);

		BookResponse book2 = new BookResponse(
			2L,
			"도서2",
			null,
			null,
			new BigDecimal(20000),
			new BigDecimal(18000),
			"9876543210987",
			LocalDate.of(2024, 6, 1),
			false,
			BookStatus.SALE,
			5,
			new AllPublishersInfoResponse(1L, "출판사"),
			false,
			List.of(),
			List.of(),
			List.of(),
			List.of()
		);

		given(bookService.getBooksByIds(bookIds)).willReturn(List.of(book1, book2));

		// when
		ResponseEntity<List<BookResponse>> result = bookController.getBooksByIds(bookIds);

		// then
		assertThat(result.getStatusCode()).isEqualTo(HttpStatus.OK);
		assertThat(result.getBody()).isNotNull();
		assertThat(result.getBody()).hasSize(2);
		assertThat(result.getBody().get(0).bookId()).isEqualTo(1L);
		assertThat(result.getBody().get(1).bookId()).isEqualTo(2L);

		verify(bookService, times(1)).getBooksByIds(bookIds);
	}

	@Test
	@DisplayName("도서 재고 조회 성공 - GET /api/books/{book-id}/stock")
	void getBookStock_Success() {
		// given
		Long bookId = 1L;
		BookStockResponse response = new BookStockResponse(bookId, "도서명", 10);
		given(bookService.getBookStock(bookId)).willReturn(response);

		// when
		ResponseEntity<BookStockResponse> result = bookController.getBookStock(bookId);

		// then
		assertThat(result.getStatusCode()).isEqualTo(HttpStatus.OK);
		assertThat(result.getBody()).isNotNull();
		assertThat(result.getBody().bookId()).isEqualTo(bookId);
		assertThat(result.getBody().bookName()).isEqualTo("도서명");
		assertThat(result.getBody().stock()).isEqualTo(10);

		verify(bookService, times(1)).getBookStock(bookId);
	}

	@Test
	@DisplayName("도서 재고 수정 성공 - PUT /api/books/{book-id}/stock")
	void updateBookStock_Success() {
		// given
		Long bookId = 1L;
		BookStockUpdateRequest request = new BookStockUpdateRequest(20);
		willDoNothing().given(bookService).updateBookStock(bookId, request);

		// when
		ResponseEntity<Void> result = bookController.updateBookStock(bookId, request);

		// then
		assertThat(result.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
		assertThat(result.getBody()).isNull();

		verify(bookService, times(1)).updateBookStock(bookId, request);
	}
}
