package com.nhnacademy.byeol23backend.bookset.book.service.impl;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import com.nhnacademy.byeol23backend.bookset.bookcategory.repository.BookCategoryRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.util.ReflectionTestUtils;

import com.nhnacademy.byeol23backend.bookset.book.domain.Book;
import com.nhnacademy.byeol23backend.bookset.book.dto.BookCreateRequest;
import com.nhnacademy.byeol23backend.bookset.book.dto.BookResponse;
import com.nhnacademy.byeol23backend.bookset.book.dto.BookUpdateRequest;
import com.nhnacademy.byeol23backend.bookset.book.exception.BookNotFoundException;
import com.nhnacademy.byeol23backend.bookset.book.exception.ISBNAlreadyExistException;
import com.nhnacademy.byeol23backend.bookset.book.repository.BookRepository;
import com.nhnacademy.byeol23backend.bookset.bookcategory.service.BookCategoryService;
import com.nhnacademy.byeol23backend.bookset.category.domain.Category;
import com.nhnacademy.byeol23backend.bookset.publisher.domain.Publisher;
import com.nhnacademy.byeol23backend.bookset.publisher.exception.PublisherNotFoundException;
import com.nhnacademy.byeol23backend.bookset.publisher.repository.PublisherRepository;

@ExtendWith(MockitoExtension.class)
class BookServieImplTest {

	@Mock
	private BookRepository bookRepository;

	@Mock
	private PublisherRepository publisherRepository;

	@Mock
	private BookCategoryService bookCategoryService;

	@Mock
	private BookCategoryRepository bookCategoryRepository;

	@Mock
	private org.springframework.context.ApplicationEventPublisher eventPublisher;

	@InjectMocks
	private BookServiceImpl bookService;

	// ========== createBook 테스트 ==========

	@Test
	@DisplayName("도서 생성 성공")
	void createBook_Success() {
		// given
		List<Long> categoryIds = new ArrayList<>();
		categoryIds.add(1L);
		categoryIds.add(2L);

		BookCreateRequest bookCreateRequest = new BookCreateRequest(
			"８월에 만나요",
			"1장, 2장, 3장, 4장, 5장, 6장",
			"결혼한 지 스물일곱 해가 된 평범한 주부 아나 막달레나 바흐는 ......",
			new BigDecimal(16000),
			new BigDecimal(15990),
			"1234567890123",
			LocalDate.of(2024, 5, 15),
			true,
			"판매중",
			12,
			1L,
			categoryIds
		);

		Publisher mockPublisher = new Publisher();
		ReflectionTestUtils.setField(mockPublisher, "publisherId", 1L);
		ReflectionTestUtils.setField(mockPublisher, "publisherName", "민음사");

		Book savedBook = new Book();
		savedBook.createBook(bookCreateRequest, mockPublisher);
		ReflectionTestUtils.setField(savedBook, "bookId", 1L);

		Category category1 = new Category("국내도서", null);
		ReflectionTestUtils.setField(category1, "categoryId", 1L);
		ReflectionTestUtils.setField(category1, "pathId", "1");
		ReflectionTestUtils.setField(category1, "pathName", "국내도서");

		Category category2 = new Category("소설", category1);
		ReflectionTestUtils.setField(category2, "categoryId", 2L);
		ReflectionTestUtils.setField(category2, "pathId", "1/2");
		ReflectionTestUtils.setField(category2, "pathName", "국내도서/소설");

		given(bookRepository.existsByIsbn("1234567890123")).willReturn(false);
		given(publisherRepository.findById(1L)).willReturn(Optional.of(mockPublisher));
		given(bookRepository.save(any(Book.class))).willReturn(savedBook);
		doNothing().when(bookCategoryService).createBookCategories(any(Book.class), anyList());
		given(bookCategoryService.getCategoriesByBookId(1L))
			.willReturn(List.of(category1, category2));

		BookResponse result = bookService.createBook(bookCreateRequest);

		assertThat(result).isNotNull();
		assertThat(result.bookId()).isEqualTo(1L);
		assertThat(result.bookName()).isEqualTo("８월에 만나요");
		assertThat(result.toc()).isEqualTo("1장, 2장, 3장, 4장, 5장, 6장");
		assertThat(result.description()).isEqualTo("결혼한 지 스물일곱 해가 된 평범한 주부 아나 막달레나 바흐는 ......");
		assertThat(result.regularPrice()).isEqualByComparingTo(new BigDecimal(16000));
		assertThat(result.salePrice()).isEqualByComparingTo(new BigDecimal(15990));
		assertThat(result.isbn()).isEqualTo("1234567890123");
		assertThat(result.publishDate()).isEqualTo(LocalDate.of(2024, 5, 15));
		assertThat(result.isPack()).isTrue();
		assertThat(result.bookStatus()).isEqualTo("판매중");
		assertThat(result.stock()).isEqualTo(12);
		assertThat(result.publisherId()).isEqualTo(1L);
		assertThat(result.categories()).hasSize(2);
		assertThat(result.categories().get(0).id()).isEqualTo(1L);
		assertThat(result.categories().get(1).id()).isEqualTo(2L);

		verify(bookRepository, times(1)).existsByIsbn("1234567890123");
		verify(publisherRepository, times(1)).findById(1L);
		verify(bookRepository, times(1)).save(any(Book.class));
		verify(bookCategoryService, times(1)).createBookCategories(any(Book.class), eq(categoryIds));
		verify(bookCategoryService, times(1)).getCategoriesByBookId(1L);
	}

	@Test
	@DisplayName("도서 생성 실패 - 중복된 ISBN")
	void createBook_Fail_DuplicateISBN() {
		// given
		BookCreateRequest bookCreateRequest = new BookCreateRequest(
			"８월에 만나요",
			"1장, 2장, 3장, 4장, 5장, 6장",
			"결혼한 지 스물일곱 해가 된 평범한 주부 아나 막달레나 바흐는 ......",
			new BigDecimal(16000),
			new BigDecimal(15990),
			"1234567890123",
			LocalDate.of(2024, 5, 15),
			true,
			"판매중",
			12,
			1L,
			List.of(1L)
		);

		given(bookRepository.existsByIsbn("1234567890123")).willReturn(true);

		// when & then
		assertThatThrownBy(() -> bookService.createBook(bookCreateRequest))
			.isInstanceOf(ISBNAlreadyExistException.class)
			.hasMessageContaining("이미 존재하는 ISBN입니다");

		verify(bookRepository, never()).save(any(Book.class));
		verify(bookCategoryService, never()).createBookCategories(any(), any());
	}

	@Test
	@DisplayName("도서 생성 실패 - 존재하지 않는 출판사")
	void createBook_Fail_PublisherNotFound() {
		// given
		BookCreateRequest bookCreateRequest = new BookCreateRequest(
			"８월에 만나요",
			"1장, 2장, 3장, 4장, 5장, 6장",
			"결혼한 지 스물일곱 해가 된 평범한 주부 아나 막달레나 바흐는 ......",
			new BigDecimal(16000),
			new BigDecimal(15990),
			"1234567890123",
			LocalDate.of(2024, 5, 15),
			true,
			"판매중",
			12,
			999L,
			List.of(1L)
		);

		given(bookRepository.existsByIsbn("1234567890123")).willReturn(false);
		given(publisherRepository.findById(999L)).willReturn(Optional.empty());

		// when & then
		assertThatThrownBy(() -> bookService.createBook(bookCreateRequest))
			.isInstanceOf(PublisherNotFoundException.class)
			.hasMessageContaining("존재하지 않는 출판사 ID입니다");

		verify(bookRepository, never()).save(any(Book.class));
		verify(bookCategoryService, never()).createBookCategories(any(), any());
	}

	// ========== getBook 테스트 ==========

	@Test
	@DisplayName("도서 조회 성공")
	void getBook_Success() {
		// given
		Long bookId = 1L;
		Publisher publisher = new Publisher();
		ReflectionTestUtils.setField(publisher, "publisherId", 1L);
		ReflectionTestUtils.setField(publisher, "publisherName", "민음사");

		Book book = new Book();
		ReflectionTestUtils.setField(book, "bookId", bookId);
		ReflectionTestUtils.setField(book, "bookName", "８월에 만나요");
		ReflectionTestUtils.setField(book, "publisher", publisher);

		Category category = new Category("국내도서", null);
		ReflectionTestUtils.setField(category, "categoryId", 1L);
		ReflectionTestUtils.setField(category, "pathId", "1");
		ReflectionTestUtils.setField(category, "pathName", "국내도서");

		given(bookRepository.findById(bookId)).willReturn(Optional.of(book));
		given(bookCategoryService.getCategoriesByBookId(bookId))
			.willReturn(List.of(category));

		// when
		BookResponse result = bookService.getBook(bookId);

		// then
		assertThat(result).isNotNull();
		assertThat(result.bookId()).isEqualTo(bookId);
		verify(bookRepository, times(1)).findById(bookId);
		verify(bookCategoryService, times(1)).getCategoriesByBookId(bookId);
	}

	@Test
	@DisplayName("도서 조회 실패 - 존재하지 않는 도서")
	void getBook_Fail_BookNotFound() {
		// given
		Long bookId = 999L;
		given(bookRepository.findById(bookId)).willReturn(Optional.empty());

		// when & then
		assertThatThrownBy(() -> bookService.getBook(bookId))
			.isInstanceOf(BookNotFoundException.class)
			.hasMessageContaining("존재하지 않는 도서입니다");

		verify(bookRepository, times(1)).findById(bookId);
		verify(bookCategoryService, never()).getCategoriesByBookId(anyLong());
	}

	@Test
	@DisplayName("도서 조회 및 조회수 증가 실패 - 존재하지 않는 도서")
	void getBookAndIncreaseViewCount_Fail_BookNotFound() {
		// given
		Long bookId = 999L;
		String viewerId = "user123";
		given(bookRepository.findById(bookId)).willReturn(Optional.empty());

		// when & then
		assertThatThrownBy(() -> bookService.getBookAndIncreaseViewCount(bookId, viewerId))
			.isInstanceOf(BookNotFoundException.class)
			.hasMessageContaining("존재하지 않는 도서입니다");

		verify(bookRepository, times(1)).findById(bookId);
		verify(eventPublisher, never()).publishEvent(any());
		verify(bookCategoryService, never()).getCategoriesByBookId(anyLong());
	}

	// ========== updateBook 테스트 ==========

	@Test
	@DisplayName("도서 수정 성공")
	void updateBook_Success() {
		// given
		Long bookId = 1L;
		List<Long> categoryIds = List.of(1L, 2L);

		BookUpdateRequest updateRequest = new BookUpdateRequest(
			"수정된 도서명",
			"수정된 목차",
			"수정된 설명",
			new BigDecimal(20000),
			new BigDecimal(19000),
			LocalDate.of(2024, 6, 1),
			false,
			"품절",
			5,
			2L,
			categoryIds
		);

		Publisher existingPublisher = new Publisher();
		ReflectionTestUtils.setField(existingPublisher, "publisherId", 1L);

		Publisher newPublisher = new Publisher();
		ReflectionTestUtils.setField(newPublisher, "publisherId", 2L);

		Book existingBook = new Book();
		ReflectionTestUtils.setField(existingBook, "bookId", bookId);
		ReflectionTestUtils.setField(existingBook, "publisher", existingPublisher);

		Category category1 = new Category("국내도서", null);
		ReflectionTestUtils.setField(category1, "categoryId", 1L);
		ReflectionTestUtils.setField(category1, "pathId", "1");
		ReflectionTestUtils.setField(category1, "pathName", "국내도서");

		Category category2 = new Category("소설", category1);
		ReflectionTestUtils.setField(category2, "categoryId", 2L);
		ReflectionTestUtils.setField(category2, "pathId", "1/2");
		ReflectionTestUtils.setField(category2, "pathName", "국내도서/소설");

		given(bookRepository.findById(bookId)).willReturn(Optional.of(existingBook));
		given(publisherRepository.findById(2L)).willReturn(Optional.of(newPublisher));
		doNothing().when(bookCategoryService).updateBookCategories(any(Book.class), anyList());
		given(bookCategoryService.getCategoriesByBookId(bookId))
			.willReturn(List.of(category1, category2));

		// when
		BookResponse result = bookService.updateBook(bookId, updateRequest);

		// then
		assertThat(result).isNotNull();
		assertThat(result.bookId()).isEqualTo(bookId);
		verify(bookRepository, times(1)).findById(bookId);
		verify(publisherRepository, times(1)).findById(2L);
		verify(bookCategoryService, times(1)).updateBookCategories(any(Book.class), eq(categoryIds));
		verify(bookCategoryService, times(1)).getCategoriesByBookId(bookId);
	}

	@Test
	@DisplayName("도서 수정 실패 - 존재하지 않는 도서")
	void updateBook_Fail_BookNotFound() {
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
			"품절",
			5,
			1L,
			List.of(1L)
		);

		given(bookRepository.findById(bookId)).willReturn(Optional.empty());

		// when & then
		assertThatThrownBy(() -> bookService.updateBook(bookId, updateRequest))
			.isInstanceOf(BookNotFoundException.class)
			.hasMessageContaining("존재하지 않는 도서입니다");

		verify(bookRepository, times(1)).findById(bookId);
		verify(publisherRepository, never()).findById(anyLong());
		verify(bookCategoryService, never()).updateBookCategories(any(), any());
	}

	@Test
	@DisplayName("도서 수정 실패 - 존재하지 않는 출판사")
	void updateBook_Fail_PublisherNotFound() {
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
			"품절",
			5,
			999L,
			List.of(1L)
		);

		Book existingBook = new Book();
		ReflectionTestUtils.setField(existingBook, "bookId", bookId);

		given(bookRepository.findById(bookId)).willReturn(Optional.of(existingBook));
		given(publisherRepository.findById(999L)).willReturn(Optional.empty());

		// when & then
		assertThatThrownBy(() -> bookService.updateBook(bookId, updateRequest))
			.isInstanceOf(PublisherNotFoundException.class)
			.hasMessageContaining("존재하지 않는 출판사 ID입니다");

		verify(bookRepository, times(1)).findById(bookId);
		verify(publisherRepository, times(1)).findById(999L);
		verify(bookCategoryService, never()).updateBookCategories(any(), any());
	}

	// ========== deleteBook 테스트 ==========

	@Test
	@DisplayName("도서 삭제 성공")
	void deleteBook_Success() {
		// given
		Long bookId = 1L;
		Book book = new Book();
		ReflectionTestUtils.setField(book, "bookId", bookId);

		given(bookRepository.findById(bookId)).willReturn(Optional.of(book));
		doNothing().when(bookRepository).delete(book);

		// when
		bookService.deleteBook(bookId);

		// then
		verify(bookRepository, times(1)).findById(bookId);
		verify(bookRepository, times(1)).delete(book);
	}

	@Test
	@DisplayName("도서 삭제 실패 - 존재하지 않는 도서")
	void deleteBook_Fail_BookNotFound() {
		// given
		Long bookId = 999L;
		given(bookRepository.findById(bookId)).willReturn(Optional.empty());

		// when & then
		assertThatThrownBy(() -> bookService.deleteBook(bookId))
			.isInstanceOf(BookNotFoundException.class)
			.hasMessageContaining("존재하지 않는 도서입니다");

		verify(bookRepository, times(1)).findById(bookId);
		verify(bookRepository, never()).delete(any(Book.class));
	}

	// ========== getBooks 테스트 ==========

//	@Test
//	@DisplayName("도서 목록 조회 성공")
//	void getBooks_Success() {
//		// given
//		Pageable pageable = PageRequest.of(0, 10);
//
//		Publisher publisher1 = new Publisher();
//		ReflectionTestUtils.setField(publisher1, "publisherId", 1L);
//
//		Publisher publisher2 = new Publisher();
//		ReflectionTestUtils.setField(publisher2, "publisherId", 2L);
//
//		Book book1 = new Book();
//		ReflectionTestUtils.setField(book1, "bookId", 1L);
//		ReflectionTestUtils.setField(book1, "bookName", "도서1");
//		ReflectionTestUtils.setField(book1, "publisher", publisher1);
//
//		Book book2 = new Book();
//		ReflectionTestUtils.setField(book2, "bookId", 2L);
//		ReflectionTestUtils.setField(book2, "bookName", "도서2");
//		ReflectionTestUtils.setField(book2, "publisher", publisher2);
//
//		Category category1 = new Category("국내도서", null);
//		ReflectionTestUtils.setField(category1, "categoryId", 1L);
//		ReflectionTestUtils.setField(category1, "pathId", "1");
//		ReflectionTestUtils.setField(category1, "pathName", "국내도서");
//
//		given(bookRepository.findAll()).willReturn(List.of(book1, book2));
//		given(bookCategoryService.getCategoriesByBookId(1L))
//			.willReturn(List.of(category1));
//		given(bookCategoryService.getCategoriesByBookId(2L))
//			.willReturn(List.of(category1));
//
//		// when
//		List<BookResponse> result = bookService.getBooks(pageable);
//
//		// then
//		assertThat(result).isNotNull();
//		assertThat(result).hasSize(2);
//		assertThat(result.get(0).bookId()).isEqualTo(1L);
//		assertThat(result.get(1).bookId()).isEqualTo(2L);
//		verify(bookRepository, times(1)).findAll();
//		verify(bookCategoryService, times(1)).getCategoriesByBookId(1L);
//		verify(bookCategoryService, times(1)).getCategoriesByBookId(2L);
//	}

	@Test
	@DisplayName("도서 목록 조회 성공 - 빈 목록")
	void getBooks_Success_EmptyList() {
		// given
		Pageable pageable = PageRequest.of(0, 10);
		given(bookRepository.findAll()).willReturn(List.of());

		// when
		List<BookResponse> result = bookService.getBooks(pageable);

		// then
		assertThat(result).isNotNull();
		assertThat(result).isEmpty();
		verify(bookRepository, times(1)).findAll();
		verify(bookCategoryService, never()).getCategoriesByBookId(anyLong());
	}
}
