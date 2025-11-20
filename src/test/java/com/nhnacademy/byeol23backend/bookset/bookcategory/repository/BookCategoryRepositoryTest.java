package com.nhnacademy.byeol23backend.bookset.bookcategory.repository;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import com.nhnacademy.byeol23backend.bookset.book.domain.Book;
import com.nhnacademy.byeol23backend.bookset.bookcategory.domain.BookCategory;
import com.nhnacademy.byeol23backend.bookset.category.domain.Category;
import com.nhnacademy.byeol23backend.bookset.publisher.domain.Publisher;

@ExtendWith(MockitoExtension.class)
class BookCategoryRepositoryTest {

	@Mock
	private BookCategoryRepository bookCategoryRepository;

	private Book testBook;
	private Category testCategory1;
	private Category testCategory2;
	private BookCategory testBookCategory;

	@BeforeEach
	void setUp() {
		// 테스트용 데이터 준비
		Publisher publisher = new Publisher();
		ReflectionTestUtils.setField(publisher, "publisherId", 1L);
		ReflectionTestUtils.setField(publisher, "publisherName", "민음사");

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

		testBookCategory = BookCategory.of(testBook, testCategory1);
		ReflectionTestUtils.setField(testBookCategory, "bookCategoryId", 1L);
	}

	@Test
	@DisplayName("existsByCategoryPathIdLike - pathId로 시작하는 카테고리가 존재하는 경우 true 반환")
	void existsByCategoryPathIdLike_WhenExists_ReturnsTrue() {
		// given
		String pathId = "1";
		given(bookCategoryRepository.existsByCategoryPathIdLike(pathId)).willReturn(true);

		// when
		boolean exists = bookCategoryRepository.existsByCategoryPathIdLike(pathId);

		// then
		assertThat(exists).isTrue();
		verify(bookCategoryRepository, times(1)).existsByCategoryPathIdLike(pathId);
	}

	@Test
	@DisplayName("existsByCategoryPathIdLike - pathId로 시작하는 카테고리가 존재하지 않는 경우 false 반환")
	void existsByCategoryPathIdLike_WhenNotExists_ReturnsFalse() {
		// given
		String pathId = "999";
		given(bookCategoryRepository.existsByCategoryPathIdLike(pathId)).willReturn(false);

		// when
		boolean exists = bookCategoryRepository.existsByCategoryPathIdLike(pathId);

		// then
		assertThat(exists).isFalse();
		verify(bookCategoryRepository, times(1)).existsByCategoryPathIdLike(pathId);
	}

	@Test
	@DisplayName("existsByCategoryPathIdLike - 하위 경로가 포함된 경우에도 true 반환")
	void existsByCategoryPathIdLike_WhenSubPathExists_ReturnsTrue() {
		// given
		String pathId = "1";
		given(bookCategoryRepository.existsByCategoryPathIdLike(pathId)).willReturn(true);

		// when
		boolean exists = bookCategoryRepository.existsByCategoryPathIdLike(pathId);

		// then
		assertThat(exists).isTrue();
		verify(bookCategoryRepository, times(1)).existsByCategoryPathIdLike(pathId);
	}

	@Test
	@DisplayName("findCategoriesByBookId - 도서 ID로 카테고리 조회 성공")
	void findCategoriesByBookId_WhenExists_ReturnsCategories() {
		// given
		Long bookId = 1L;
		List<Category> categories = List.of(testCategory1, testCategory2);
		given(bookCategoryRepository.findCategoriesByBookId(bookId)).willReturn(categories);

		// when
		List<Category> result = bookCategoryRepository.findCategoriesByBookId(bookId);

		// then
		assertThat(result).isNotNull();
		assertThat(result).hasSize(2);
		assertThat(result.get(0).getCategoryId()).isEqualTo(1L);
		assertThat(result.get(1).getCategoryId()).isEqualTo(2L);
		assertThat(result.get(0).getCategoryName()).isEqualTo("국내도서");
		assertThat(result.get(1).getCategoryName()).isEqualTo("소설");
		verify(bookCategoryRepository, times(1)).findCategoriesByBookId(bookId);
	}

	@Test
	@DisplayName("findCategoriesByBookId - 카테고리가 없는 경우 빈 리스트 반환")
	void findCategoriesByBookId_WhenNoCategories_ReturnsEmptyList() {
		// given
		Long bookId = 999L;
		given(bookCategoryRepository.findCategoriesByBookId(bookId)).willReturn(new ArrayList<>());

		// when
		List<Category> result = bookCategoryRepository.findCategoriesByBookId(bookId);

		// then
		assertThat(result).isNotNull();
		assertThat(result).isEmpty();
		verify(bookCategoryRepository, times(1)).findCategoriesByBookId(bookId);
	}

	@Test
	@DisplayName("findCategoriesByBookId - 단일 카테고리만 존재하는 경우")
	void findCategoriesByBookId_WhenSingleCategory_ReturnsSingleCategory() {
		// given
		Long bookId = 1L;
		List<Category> categories = List.of(testCategory1);
		given(bookCategoryRepository.findCategoriesByBookId(bookId)).willReturn(categories);

		// when
		List<Category> result = bookCategoryRepository.findCategoriesByBookId(bookId);

		// then
		assertThat(result).isNotNull();
		assertThat(result).hasSize(1);
		assertThat(result.get(0).getCategoryId()).isEqualTo(1L);
		verify(bookCategoryRepository, times(1)).findCategoriesByBookId(bookId);
	}

	@Test
	@DisplayName("deleteByBookId - 도서 ID로 BookCategory 삭제 성공")
	void deleteByBookId_Success() {
		// given
		Long bookId = 1L;
		doNothing().when(bookCategoryRepository).deleteByBookId(bookId);

		// when
		bookCategoryRepository.deleteByBookId(bookId);

		// then
		verify(bookCategoryRepository, times(1)).deleteByBookId(bookId);
	}

	@Test
	@DisplayName("deleteByBookId - 존재하지 않는 도서 ID로 삭제 시도")
	void deleteByBookId_WhenBookNotExists_Success() {
		// given
		Long bookId = 999L;
		doNothing().when(bookCategoryRepository).deleteByBookId(bookId);

		// when
		bookCategoryRepository.deleteByBookId(bookId);

		// then
		verify(bookCategoryRepository, times(1)).deleteByBookId(bookId);
	}

	@Test
	@DisplayName("save - BookCategory 저장 테스트")
	void saveBookCategory() {
		// given
		BookCategory newBookCategory = BookCategory.of(testBook, testCategory1);
		given(bookCategoryRepository.save(any(BookCategory.class))).willReturn(newBookCategory);

		// when
		BookCategory savedBookCategory = bookCategoryRepository.save(newBookCategory);

		// then
		assertThat(savedBookCategory).isNotNull();
		verify(bookCategoryRepository, times(1)).save(newBookCategory);
	}

	@Test
	@DisplayName("findById - BookCategory 조회 테스트")
	void findById() {
		// given
		Long bookCategoryId = 1L;
		given(bookCategoryRepository.findById(bookCategoryId)).willReturn(Optional.of(testBookCategory));

		// when
		Optional<BookCategory> result = bookCategoryRepository.findById(bookCategoryId);

		// then
		assertThat(result).isPresent();
		assertThat(result.get().getBookCategoryId()).isEqualTo(1L);
		verify(bookCategoryRepository, times(1)).findById(bookCategoryId);
	}

	@Test
	@DisplayName("findById - 존재하지 않는 BookCategory 조회 시 빈 Optional 반환")
	void findById_WhenNotExists_ReturnsEmpty() {
		// given
		Long bookCategoryId = 999L;
		given(bookCategoryRepository.findById(bookCategoryId)).willReturn(Optional.empty());

		// when
		Optional<BookCategory> result = bookCategoryRepository.findById(bookCategoryId);

		// then
		assertThat(result).isEmpty();
		verify(bookCategoryRepository, times(1)).findById(bookCategoryId);
	}

	@Test
	@DisplayName("findAll - BookCategory 전체 조회 테스트")
	void findAllBookCategories() {
		// given
		BookCategory bookCategory1 = BookCategory.of(testBook, testCategory1);
		BookCategory bookCategory2 = BookCategory.of(testBook, testCategory2);
		List<BookCategory> bookCategories = List.of(bookCategory1, bookCategory2);
		given(bookCategoryRepository.findAll()).willReturn(bookCategories);

		// when
		List<BookCategory> result = bookCategoryRepository.findAll();

		// then
		assertThat(result).hasSize(2);
		verify(bookCategoryRepository, times(1)).findAll();
	}

	@Test
	@DisplayName("delete - BookCategory 삭제 테스트")
	void deleteBookCategory() {
		// given
		BookCategory bookCategoryToDelete = testBookCategory;
		doNothing().when(bookCategoryRepository).delete(any(BookCategory.class));

		// when
		bookCategoryRepository.delete(bookCategoryToDelete);

		// then
		verify(bookCategoryRepository, times(1)).delete(bookCategoryToDelete);
	}
}
