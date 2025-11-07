package com.nhnacademy.byeol23backend.bookset.bookcategory.service.impl;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.*;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import com.nhnacademy.byeol23backend.bookset.book.domain.Book;
import com.nhnacademy.byeol23backend.bookset.bookcategory.domain.BookCategory;
import com.nhnacademy.byeol23backend.bookset.bookcategory.repository.BookCategoryRepository;
import com.nhnacademy.byeol23backend.bookset.category.domain.Category;
import com.nhnacademy.byeol23backend.bookset.category.repository.CategoryRepository;
import com.nhnacademy.byeol23backend.bookset.publisher.domain.Publisher;

@ExtendWith(MockitoExtension.class)
class BookCategoryServiceImplTest {

	@Mock
	private BookCategoryRepository bookCategoryRepository;

	@Mock
	private CategoryRepository categoryRepository;

	@InjectMocks
	private BookCategoryServiceImpl bookCategoryService;

	private Book testBook;
	private Category testCategory1;
	private Category testCategory2;
	private Publisher testPublisher;

	@BeforeEach
	void setUp() {
		// 테스트용 데이터 준비
		testPublisher = new Publisher();
		ReflectionTestUtils.setField(testPublisher, "publisherId", 1L);
		ReflectionTestUtils.setField(testPublisher, "publisherName", "민음사");

		testBook = new Book();
		ReflectionTestUtils.setField(testBook, "bookId", 1L);
		ReflectionTestUtils.setField(testBook, "bookName", "테스트 도서");

		testCategory1 = new Category("국내도서", null);
		ReflectionTestUtils.setField(testCategory1, "categoryId", 1L);
		ReflectionTestUtils.setField(testCategory1, "pathId", "1");
		ReflectionTestUtils.setField(testCategory1, "pathName", "국내도서");

		testCategory2 = new Category("소설", testCategory1);
		ReflectionTestUtils.setField(testCategory2, "categoryId", 2L);
		ReflectionTestUtils.setField(testCategory2, "pathId", "1/2");
		ReflectionTestUtils.setField(testCategory2, "pathName", "국내도서/소설");
	}

	// ========== getCategoriesByBookId 테스트 ==========

	@Test
	@DisplayName("getCategoriesByBookId - 도서 ID로 카테고리 조회 성공")
	void getCategoriesByBookId_Success() {
		// given
		Long bookId = 1L;
		List<Category> categories = List.of(testCategory1, testCategory2);
		given(bookCategoryRepository.findCategoriesByBookId(bookId)).willReturn(categories);

		// when
		List<Category> result = bookCategoryService.getCategoriesByBookId(bookId);

		// then
		assertThat(result).isNotNull();
		assertThat(result).hasSize(2);
		assertThat(result.get(0).getCategoryId()).isEqualTo(1L);
		assertThat(result.get(1).getCategoryId()).isEqualTo(2L);
		verify(bookCategoryRepository, times(1)).findCategoriesByBookId(bookId);
	}

	@Test
	@DisplayName("getCategoriesByBookId - 카테고리가 없는 경우 빈 리스트 반환")
	void getCategoriesByBookId_WhenNoCategories_ReturnsEmptyList() {
		// given
		Long bookId = 999L;
		given(bookCategoryRepository.findCategoriesByBookId(bookId)).willReturn(new ArrayList<>());

		// when
		List<Category> result = bookCategoryService.getCategoriesByBookId(bookId);

		// then
		assertThat(result).isNotNull();
		assertThat(result).isEmpty();
		verify(bookCategoryRepository, times(1)).findCategoriesByBookId(bookId);
	}

	// ========== createBookCategories 테스트 ==========

	@Test
	@DisplayName("createBookCategories - 도서에 카테고리 연결 성공")
	void createBookCategories_Success() {
		// given
		List<Long> categoryIds = List.of(1L, 2L);
		List<Category> categories = List.of(testCategory1, testCategory2);
		List<BookCategory> bookCategories = List.of(
			BookCategory.of(testBook, testCategory1),
			BookCategory.of(testBook, testCategory2)
		);

		given(categoryRepository.findAllById(categoryIds)).willReturn(categories);
		given(bookCategoryRepository.saveAll(anyList())).willReturn(bookCategories);

		// when
		bookCategoryService.createBookCategories(testBook, categoryIds);

		// then
		verify(categoryRepository, times(1)).findAllById(categoryIds);
		verify(bookCategoryRepository, times(1)).saveAll(anyList());
	}

	@Test
	@DisplayName("createBookCategories - 카테고리 ID 리스트가 null인 경우 아무 작업도 하지 않음")
	void createBookCategories_WhenCategoryIdsIsNull_DoesNothing() {
		// when
		bookCategoryService.createBookCategories(testBook, null);

		// then
		verify(categoryRepository, never()).findAllById(anyList());
		verify(bookCategoryRepository, never()).saveAll(anyList());
	}

	@Test
	@DisplayName("createBookCategories - 카테고리 ID 리스트가 비어있는 경우 아무 작업도 하지 않음")
	void createBookCategories_WhenCategoryIdsIsEmpty_DoesNothing() {
		// when
		bookCategoryService.createBookCategories(testBook, new ArrayList<>());

		// then
		verify(categoryRepository, never()).findAllById(anyList());
		verify(bookCategoryRepository, never()).saveAll(anyList());
	}

	@Test
	@DisplayName("createBookCategories - 존재하지 않는 카테고리 ID는 무시되고 존재하는 것만 저장")
	void createBookCategories_WhenSomeCategoriesNotFound_SavesOnlyExisting() {
		// given
		List<Long> categoryIds = List.of(1L, 999L);
		List<Category> foundCategories = List.of(testCategory1); // 999L은 없음
		List<BookCategory> bookCategories = List.of(
			BookCategory.of(testBook, testCategory1)
		);

		given(categoryRepository.findAllById(categoryIds)).willReturn(foundCategories);
		given(bookCategoryRepository.saveAll(anyList())).willReturn(bookCategories);

		// when
		bookCategoryService.createBookCategories(testBook, categoryIds);

		// then
		verify(categoryRepository, times(1)).findAllById(categoryIds);
		verify(bookCategoryRepository, times(1)).saveAll(anyList());
	}

	@Test
	@DisplayName("createBookCategories - 단일 카테고리 연결 성공")
	void createBookCategories_Success_SingleCategory() {
		// given
		List<Long> categoryIds = List.of(1L);
		List<Category> categories = List.of(testCategory1);
		List<BookCategory> bookCategories = List.of(
			BookCategory.of(testBook, testCategory1)
		);

		given(categoryRepository.findAllById(categoryIds)).willReturn(categories);
		given(bookCategoryRepository.saveAll(anyList())).willReturn(bookCategories);

		// when
		bookCategoryService.createBookCategories(testBook, categoryIds);

		// then
		verify(categoryRepository, times(1)).findAllById(categoryIds);
		verify(bookCategoryRepository, times(1)).saveAll(anyList());
	}

	// ========== updateBookCategories 테스트 ==========

	@Test
	@DisplayName("updateBookCategories - 도서 카테고리 업데이트 성공")
	void updateBookCategories_Success() {
		// given
		List<Long> categoryIds = List.of(1L, 2L);
		List<Category> categories = List.of(testCategory1, testCategory2);
		List<BookCategory> bookCategories = List.of(
			BookCategory.of(testBook, testCategory1),
			BookCategory.of(testBook, testCategory2)
		);

		doNothing().when(bookCategoryRepository).deleteByBookId(testBook.getBookId());
		given(categoryRepository.findAllById(categoryIds)).willReturn(categories);
		given(bookCategoryRepository.saveAll(anyList())).willReturn(bookCategories);

		// when
		bookCategoryService.updateBookCategories(testBook, categoryIds);

		// then
		verify(bookCategoryRepository, times(1)).deleteByBookId(testBook.getBookId());
		verify(categoryRepository, times(1)).findAllById(categoryIds);
		verify(bookCategoryRepository, times(1)).saveAll(anyList());
	}

	@Test
	@DisplayName("updateBookCategories - 카테고리 ID 리스트가 null인 경우 삭제만 수행하고 빈 리스트로 findAllById 호출")
	void updateBookCategories_WhenCategoryIdsIsNull_DeletesAndCallsFindAllById() {
		// given
		doNothing().when(bookCategoryRepository).deleteByBookId(testBook.getBookId());
		given(categoryRepository.findAllById(null)).willReturn(new ArrayList<>());

		// when
		bookCategoryService.updateBookCategories(testBook, null);

		// then
		verify(bookCategoryRepository, times(1)).deleteByBookId(testBook.getBookId());
		verify(categoryRepository, times(1)).findAllById(null);
		verify(bookCategoryRepository, times(1)).saveAll(anyList());
	}

	@Test
	@DisplayName("updateBookCategories - 카테고리 ID 리스트가 비어있는 경우 삭제만 수행하고 빈 리스트로 findAllById 호출")
	void updateBookCategories_WhenCategoryIdsIsEmpty_DeletesAndCallsFindAllById() {
		// given
		List<Long> emptyList = new ArrayList<>();
		doNothing().when(bookCategoryRepository).deleteByBookId(testBook.getBookId());
		given(categoryRepository.findAllById(emptyList)).willReturn(new ArrayList<>());

		// when
		bookCategoryService.updateBookCategories(testBook, emptyList);

		// then
		verify(bookCategoryRepository, times(1)).deleteByBookId(testBook.getBookId());
		verify(categoryRepository, times(1)).findAllById(emptyList);
		verify(bookCategoryRepository, times(1)).saveAll(anyList());
	}

	@Test
	@DisplayName("updateBookCategories - 존재하지 않는 카테고리 ID는 무시되고 존재하는 것만 저장")
	void updateBookCategories_WhenSomeCategoriesNotFound_SavesOnlyExisting() {
		// given
		List<Long> categoryIds = List.of(1L, 999L);
		List<Category> foundCategories = List.of(testCategory1); // 999L은 없음
		List<BookCategory> bookCategories = List.of(
			BookCategory.of(testBook, testCategory1)
		);

		doNothing().when(bookCategoryRepository).deleteByBookId(testBook.getBookId());
		given(categoryRepository.findAllById(categoryIds)).willReturn(foundCategories);
		given(bookCategoryRepository.saveAll(anyList())).willReturn(bookCategories);

		// when
		bookCategoryService.updateBookCategories(testBook, categoryIds);

		// then
		verify(bookCategoryRepository, times(1)).deleteByBookId(testBook.getBookId());
		verify(categoryRepository, times(1)).findAllById(categoryIds);
		verify(bookCategoryRepository, times(1)).saveAll(anyList());
	}

	@Test
	@DisplayName("updateBookCategories - 모든 카테고리 제거 후 새 카테고리 추가")
	void updateBookCategories_ReplaceAllCategories_Success() {
		// given
		List<Long> newCategoryIds = List.of(2L);
		List<Category> categories = List.of(testCategory2);
		List<BookCategory> bookCategories = List.of(
			BookCategory.of(testBook, testCategory2)
		);

		doNothing().when(bookCategoryRepository).deleteByBookId(testBook.getBookId());
		given(categoryRepository.findAllById(newCategoryIds)).willReturn(categories);
		given(bookCategoryRepository.saveAll(anyList())).willReturn(bookCategories);

		// when
		bookCategoryService.updateBookCategories(testBook, newCategoryIds);

		// then
		verify(bookCategoryRepository, times(1)).deleteByBookId(testBook.getBookId());
		verify(categoryRepository, times(1)).findAllById(newCategoryIds);
		verify(bookCategoryRepository, times(1)).saveAll(anyList());
	}
}