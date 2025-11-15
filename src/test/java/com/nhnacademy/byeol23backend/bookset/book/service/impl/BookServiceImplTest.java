// package com.nhnacademy.byeol23backend.bookset.book.service.impl;
//
// import static org.assertj.core.api.Assertions.*;
// import static org.mockito.ArgumentMatchers.*;
// import static org.mockito.BDDMockito.*;
// import static org.mockito.Mockito.lenient;
//
// import java.math.BigDecimal;
// import java.time.LocalDate;
// import java.util.ArrayList;
// import java.util.List;
// import java.util.Optional;
//
// import org.junit.jupiter.api.DisplayName;
// import org.junit.jupiter.api.Test;
// import org.junit.jupiter.api.extension.ExtendWith;
// import org.mockito.InjectMocks;
// import org.mockito.Mock;
// import org.mockito.junit.jupiter.MockitoExtension;
// import org.springframework.context.ApplicationEventPublisher;
// import org.springframework.data.domain.PageRequest;
// import org.springframework.data.domain.Pageable;
// import org.springframework.test.util.ReflectionTestUtils;
//
// import com.nhnacademy.byeol23backend.bookset.book.domain.Book;
// import com.nhnacademy.byeol23backend.bookset.book.domain.BookStatus;
// import com.nhnacademy.byeol23backend.bookset.book.dto.BookCreateRequest;
// import com.nhnacademy.byeol23backend.bookset.book.dto.BookResponse;
// import com.nhnacademy.byeol23backend.bookset.book.dto.BookStockResponse;
// import com.nhnacademy.byeol23backend.bookset.book.dto.BookStockUpdateRequest;
// import com.nhnacademy.byeol23backend.bookset.book.dto.BookUpdateRequest;
// import com.nhnacademy.byeol23backend.bookset.book.exception.BookNotFoundException;
// import com.nhnacademy.byeol23backend.bookset.book.exception.ISBNAlreadyExistException;
// import com.nhnacademy.byeol23backend.bookset.book.repository.BookRepository;
// import com.nhnacademy.byeol23backend.bookset.bookcategory.repository.BookCategoryRepository;
// import com.nhnacademy.byeol23backend.bookset.bookcategory.service.BookCategoryService;
// import com.nhnacademy.byeol23backend.bookset.bookcontributor.repository.BookContributorRepository;
// import com.nhnacademy.byeol23backend.bookset.bookcontributor.service.BookContributorService;
// import com.nhnacademy.byeol23backend.bookset.bookimage.service.BookImageServiceImpl;
// import com.nhnacademy.byeol23backend.bookset.booktag.repository.BookTagRepository;
// import com.nhnacademy.byeol23backend.bookset.booktag.service.BookTagService;
// import com.nhnacademy.byeol23backend.bookset.category.domain.Category;
// import com.nhnacademy.byeol23backend.bookset.contributor.domain.Contributor;
// import com.nhnacademy.byeol23backend.bookset.contributor.domain.ContributorRole;
// import com.nhnacademy.byeol23backend.bookset.publisher.domain.Publisher;
// import com.nhnacademy.byeol23backend.bookset.publisher.exception.PublisherNotFoundException;
// import com.nhnacademy.byeol23backend.bookset.publisher.repository.PublisherRepository;
// import com.nhnacademy.byeol23backend.bookset.tag.domain.Tag;
//
// @ExtendWith(MockitoExtension.class)
// class BookServiceImplTest {
//
// 	@Mock
// 	private BookRepository bookRepository;
//
// 	@Mock
// 	private PublisherRepository publisherRepository;
//
// 	@Mock
// 	private ApplicationEventPublisher eventPublisher;
//
// 	@Mock
// 	private BookCategoryRepository bookCategoryRepository;
//
// 	@Mock
// 	private BookCategoryService bookCategoryService;
//
// 	@Mock
// 	private BookContributorRepository bookContributorRepository;
//
// 	@Mock
// 	private BookTagRepository bookTagRepository;
//
// 	@Mock
// 	private BookTagService bookTagService;
//
// 	@Mock
// 	private BookContributorService bookContributorService;
//
// 	@Mock
// 	private BookImageServiceImpl bookImageService;
//
// 	@InjectMocks
// 	private BookServiceImpl bookService;
//
// 	@Test
// 	@DisplayName("도서 생성 성공")
// 	void createBook_Success() {
// 		// given
// 		List<Long> categoryIds = List.of(1L, 2L);
// 		List<Long> tagIds = List.of(1L);
// 		List<Long> contributorIds = List.of(1L);
//
// 		BookCreateRequest bookCreateRequest = new BookCreateRequest(
// 			"８월에 만나요",
// 			"1장, 2장, 3장, 4장, 5장, 6장",
// 			"결혼한 지 스물일곱 해가 된 평범한 주부 아나 막달레나 바흐는 ......",
// 			new BigDecimal(16000),
// 			new BigDecimal(15990),
// 			"9781234567890",
// 			LocalDate.of(2024, 5, 15),
// 			true,
// 			BookStatus.SALE,
// 			12,
// 			1L,
// 			categoryIds,
// 			tagIds,
// 			contributorIds,
// 			List.of()
// 		);
//
// 		Publisher mockPublisher = new Publisher("민음사");
// 		ReflectionTestUtils.setField(mockPublisher, "publisherId", 1L);
//
// 		// Category, Tag, Contributor 생성
// 		Category category1 = new Category("국내도서", null);
// 		ReflectionTestUtils.setField(category1, "categoryId", 1L);
// 		ReflectionTestUtils.setField(category1, "pathId", "1");
// 		ReflectionTestUtils.setField(category1, "pathName", "국내도서");
//
// 		Tag tag = new Tag("베스트셀러");
// 		ReflectionTestUtils.setField(tag, "tagId", 1L);
//
// 		Contributor contributor = new Contributor(1L, "저자명", ContributorRole.AUTHOR);
//
// 		given(bookRepository.existsByIsbn("9781234567890")).willReturn(false);
// 		given(publisherRepository.findById(1L)).willReturn(Optional.of(mockPublisher));
//
// 		// bookRepository.save()가 호출될 때 book 객체에 관계 데이터를 설정하고 반환
// 		given(bookRepository.save(any(Book.class))).willAnswer(invocation -> {
// 			Book book = invocation.getArgument(0);
// 			ReflectionTestUtils.setField(book, "bookId", 1L);
//
// 			// BookCategory, BookTag, BookContributor 생성 및 설정
// 			List<com.nhnacademy.byeol23backend.bookset.bookcategory.domain.BookCategory> bookCategories = new ArrayList<>();
// 			com.nhnacademy.byeol23backend.bookset.bookcategory.domain.BookCategory bookCategory =
// 				com.nhnacademy.byeol23backend.bookset.bookcategory.domain.BookCategory.of(book, category1);
// 			bookCategories.add(bookCategory);
// 			ReflectionTestUtils.setField(book, "bookCategories", bookCategories);
//
// 			List<com.nhnacademy.byeol23backend.bookset.booktag.domain.BookTag> bookTags = new ArrayList<>();
// 			com.nhnacademy.byeol23backend.bookset.booktag.domain.BookTag bookTag =
// 				com.nhnacademy.byeol23backend.bookset.booktag.domain.BookTag.of(book, tag);
// 			bookTags.add(bookTag);
// 			ReflectionTestUtils.setField(book, "bookTags", bookTags);
//
// 			List<com.nhnacademy.byeol23backend.bookset.bookcontributor.domain.BookContributor> bookContributors = new ArrayList<>();
// 			com.nhnacademy.byeol23backend.bookset.bookcontributor.domain.BookContributor bookContributor =
// 				com.nhnacademy.byeol23backend.bookset.bookcontributor.domain.BookContributor.of(book, contributor);
// 			bookContributors.add(bookContributor);
// 			ReflectionTestUtils.setField(book, "bookContributors", bookContributors);
//
// 			return book;
// 		});
// 		doNothing().when(bookCategoryService).createBookCategories(any(Book.class), anyList());
// 		doNothing().when(bookTagService).createBookTags(any(Book.class), anyList());
// 		doNothing().when(bookContributorService).createBookContributors(any(Book.class), anyList());
// 		given(bookCategoryService.getCategoriesByBookId(1L)).willReturn(List.of(category1));
// 		given(bookTagService.getTagsByBookId(1L)).willReturn(List.of(tag));
// 		given(bookContributorService.getContributorsByBookId(1L)).willReturn(List.of(contributor));
// 		given(bookImageService.getImageUrlsById(1L)).willReturn(List.of());
// 		lenient().doNothing().when(eventPublisher).publishEvent(any());
//
// 		// when
// 		BookResponse result = bookService.createBook(bookCreateRequest);
//
// 		// then
// 		assertThat(result).isNotNull();
// 		assertThat(result.bookId()).isEqualTo(1L);
// 		assertThat(result.bookName()).isEqualTo("８월에 만나요");
// 		assertThat(result.isbn()).isEqualTo("9781234567890");
//
// 		verify(bookRepository, times(1)).existsByIsbn("9781234567890");
// 		verify(publisherRepository, times(2)).findById(1L);
// 		verify(bookRepository, times(1)).save(any(Book.class));
// 		verify(bookCategoryService, times(1)).createBookCategories(any(Book.class), eq(categoryIds));
// 		verify(bookTagService, times(1)).createBookTags(any(Book.class), eq(tagIds));
// 		verify(bookContributorService, times(1)).createBookContributors(any(Book.class), eq(contributorIds));
// 	}
//
// 	@Test
// 	@DisplayName("도서 생성 실패 - 중복된 ISBN")
// 	void createBook_Fail_DuplicateISBN() {
// 		// given
// 		BookCreateRequest bookCreateRequest = new BookCreateRequest(
// 			"８월에 만나요",
// 			"1장, 2장, 3장",
// 			"설명",
// 			new BigDecimal(16000),
// 			new BigDecimal(15990),
// 			"9781234567890",
// 			LocalDate.of(2024, 5, 15),
// 			true,
// 			BookStatus.SALE,
// 			12,
// 			1L,
// 			List.of(1L),
// 			List.of(1L),
// 			List.of(1L),
// 			List.of()
// 		);
//
// 		given(bookRepository.existsByIsbn("9781234567890")).willReturn(true);
//
// 		// when & then
// 		assertThatThrownBy(() -> bookService.createBook(bookCreateRequest))
// 			.isInstanceOf(ISBNAlreadyExistException.class)
// 			.hasMessageContaining("이미 존재하는 ISBN입니다");
//
// 		verify(bookRepository, never()).save(any(Book.class));
// 		verify(bookCategoryService, never()).createBookCategories(any(), any());
// 	}
//
// 	@Test
// 	@DisplayName("도서 생성 실패 - 존재하지 않는 출판사")
// 	void createBook_Fail_PublisherNotFound() {
// 		// given
// 		BookCreateRequest bookCreateRequest = new BookCreateRequest(
// 			"８월에 만나요",
// 			"1장, 2장, 3장",
// 			"설명",
// 			new BigDecimal(16000),
// 			new BigDecimal(15990),
// 			"9781234567890",
// 			LocalDate.of(2024, 5, 15),
// 			true,
// 			BookStatus.SALE,
// 			12,
// 			999L,
// 			List.of(1L),
// 			List.of(1L),
// 			List.of(1L),
// 			List.of()
// 		);
//
// 		given(bookRepository.existsByIsbn("9781234567890")).willReturn(false);
// 		given(publisherRepository.findById(999L)).willReturn(Optional.empty());
//
// 		// when & then
// 		assertThatThrownBy(() -> bookService.createBook(bookCreateRequest))
// 			.isInstanceOf(PublisherNotFoundException.class)
// 			.hasMessageContaining("존재하지 않는 출판사 ID입니다");
//
// 		verify(bookRepository, never()).save(any(Book.class));
// 		verify(bookCategoryService, never()).createBookCategories(any(), any());
// 	}
//
// 	@Test
// 	@DisplayName("도서 조회 성공")
// 	void getBook_Success() {
// 		// given
// 		Long bookId = 1L;
// 		Publisher publisher = new Publisher("민음사");
// 		ReflectionTestUtils.setField(publisher, "publisherId", 1L);
//
// 		Book book = new Book();
// 		ReflectionTestUtils.setField(book, "bookId", bookId);
// 		ReflectionTestUtils.setField(book, "bookName", "８월에 만나요");
// 		ReflectionTestUtils.setField(book, "publisher", publisher);
//
// 		Category category = new Category("국내도서", null);
// 		ReflectionTestUtils.setField(category, "categoryId", 1L);
//
// 		given(bookRepository.findById(bookId)).willReturn(Optional.of(book));
// 		given(bookCategoryService.getCategoriesByBookId(bookId)).willReturn(List.of(category));
// 		given(bookTagService.getTagsByBookId(bookId)).willReturn(List.of());
// 		given(bookContributorService.getContributorsByBookId(bookId)).willReturn(List.of());
// 		given(publisherRepository.findById(1L)).willReturn(Optional.of(publisher));
// 		given(bookImageService.getImageUrlsById(bookId)).willReturn(List.of());
//
// 		// when
// 		BookResponse result = bookService.getBook(bookId);
//
// 		// then
// 		assertThat(result).isNotNull();
// 		assertThat(result.bookId()).isEqualTo(bookId);
// 		verify(bookRepository, times(1)).findById(bookId);
// 		verify(bookCategoryService, times(1)).getCategoriesByBookId(bookId);
// 	}
//
// 	@Test
// 	@DisplayName("도서 조회 실패 - 존재하지 않는 도서")
// 	void getBook_Fail_BookNotFound() {
// 		// given
// 		Long bookId = 999L;
// 		given(bookRepository.findById(bookId)).willReturn(Optional.empty());
//
// 		// when & then
// 		assertThatThrownBy(() -> bookService.getBook(bookId))
// 			.isInstanceOf(BookNotFoundException.class)
// 			.hasMessageContaining("존재하지 않는 도서입니다");
//
// 		verify(bookRepository, times(1)).findById(bookId);
// 		verify(bookCategoryService, never()).getCategoriesByBookId(anyLong());
// 	}
//
// 	@Test
// 	@DisplayName("도서 조회 및 조회수 증가")
// 	void getBookAndIncreaseViewCount_Success() {
// 		// given
// 		Long bookId = 1L;
// 		String viewerId = "user123";
// 		Publisher publisher = new Publisher("민음사");
// 		ReflectionTestUtils.setField(publisher, "publisherId", 1L);
//
// 		Book book = new Book();
// 		ReflectionTestUtils.setField(book, "bookId", bookId);
// 		ReflectionTestUtils.setField(book, "bookName", "８월에 만나요");
// 		ReflectionTestUtils.setField(book, "publisher", publisher);
//
// 		given(bookRepository.findById(bookId)).willReturn(Optional.of(book));
// 		given(bookCategoryService.getCategoriesByBookId(bookId)).willReturn(List.of());
// 		given(bookTagService.getTagsByBookId(bookId)).willReturn(List.of());
// 		given(bookContributorService.getContributorsByBookId(bookId)).willReturn(List.of());
// 		given(publisherRepository.findById(1L)).willReturn(Optional.of(publisher));
// 		given(bookImageService.getImageUrlsById(bookId)).willReturn(List.of());
// 		lenient().doNothing().when(eventPublisher).publishEvent(any());
//
// 		// when
// 		BookResponse result = bookService.getBookAndIncreaseViewCount(bookId, viewerId);
//
// 		// then
// 		assertThat(result).isNotNull();
// 		assertThat(result.bookId()).isEqualTo(bookId);
// 		// lenient stubbing을 사용했기 때문에 verify는 생략
// 	}
//
// 	@Test
// 	@DisplayName("도서 수정 성공")
// 	void updateBook_Success() {
// 		// given
// 		Long bookId = 1L;
// 		List<Long> categoryIds = List.of(1L, 2L);
// 		List<Long> tagIds = List.of(1L);
// 		List<Long> contributorIds = List.of(1L);
//
// 		BookUpdateRequest updateRequest = new BookUpdateRequest(
// 			"수정된 도서명",
// 			"수정된 목차",
// 			"수정된 설명",
// 			new BigDecimal(20000),
// 			new BigDecimal(19000),
// 			LocalDate.of(2024, 6, 1),
// 			false,
// 			BookStatus.SALE,
// 			2L,
// 			categoryIds,
// 			tagIds,
// 			contributorIds,
// 			List.of()
// 		);
//
// 		Publisher existingPublisher = new Publisher("기존 출판사");
// 		ReflectionTestUtils.setField(existingPublisher, "publisherId", 1L);
//
// 		Publisher newPublisher = new Publisher("새 출판사");
// 		ReflectionTestUtils.setField(newPublisher, "publisherId", 2L);
//
// 		Book existingBook = new Book();
// 		ReflectionTestUtils.setField(existingBook, "bookId", bookId);
// 		ReflectionTestUtils.setField(existingBook, "publisher", existingPublisher);
//
// 		Category category1 = new Category("국내도서", null);
// 		ReflectionTestUtils.setField(category1, "categoryId", 1L);
//
// 		given(bookRepository.findById(bookId)).willReturn(Optional.of(existingBook));
// 		given(publisherRepository.findById(2L)).willReturn(Optional.of(newPublisher));
// 		doNothing().when(bookCategoryService).updateBookCategories(any(Book.class), anyList());
// 		doNothing().when(bookTagService).updateBookTags(any(Book.class), anyList());
// 		doNothing().when(bookContributorService).updateBookContributors(any(Book.class), anyList());
// 		given(bookCategoryService.getCategoriesByBookId(bookId)).willReturn(List.of(category1));
// 		given(bookTagService.getTagsByBookId(bookId)).willReturn(List.of());
// 		given(bookContributorService.getContributorsByBookId(bookId)).willReturn(List.of());
// 		given(bookImageService.getImageUrlsById(bookId)).willReturn(List.of());
//
// 		// when
// 		BookResponse result = bookService.updateBook(bookId, updateRequest);
//
// 		// then
// 		assertThat(result).isNotNull();
// 		assertThat(result.bookId()).isEqualTo(bookId);
// 		verify(bookRepository, times(1)).findById(bookId);
// 		verify(publisherRepository, times(2)).findById(2L);
// 		verify(bookCategoryService, times(1)).updateBookCategories(any(Book.class), eq(categoryIds));
// 	}
//
// 	@Test
// 	@DisplayName("도서 수정 실패 - 존재하지 않는 도서")
// 	void updateBook_Fail_BookNotFound() {
// 		// given
// 		Long bookId = 999L;
// 		BookUpdateRequest updateRequest = new BookUpdateRequest(
// 			"수정된 도서명",
// 			"수정된 목차",
// 			"수정된 설명",
// 			new BigDecimal(20000),
// 			new BigDecimal(19000),
// 			LocalDate.of(2024, 6, 1),
// 			false,
// 			BookStatus.SALE,
// 			1L,
// 			List.of(1L),
// 			List.of(1L),
// 			List.of(1L),
// 			List.of()
// 		);
//
// 		given(bookRepository.findById(bookId)).willReturn(Optional.empty());
//
// 		// when & then
// 		assertThatThrownBy(() -> bookService.updateBook(bookId, updateRequest))
// 			.isInstanceOf(BookNotFoundException.class)
// 			.hasMessageContaining("존재하지 않는 도서입니다");
//
// 		verify(bookRepository, times(1)).findById(bookId);
// 		verify(publisherRepository, never()).findById(anyLong());
// 		verify(bookCategoryService, never()).updateBookCategories(any(), any());
// 	}
//
// 	@Test
// 	@DisplayName("도서 삭제 성공")
// 	void deleteBook_Success() {
// 		// given
// 		Long bookId = 1L;
// 		Book book = new Book();
// 		ReflectionTestUtils.setField(book, "bookId", bookId);
//
// 		given(bookRepository.findById(bookId)).willReturn(Optional.of(book));
// 		doNothing().when(bookRepository).delete(book);
// 		doNothing().when(bookCategoryRepository).deleteByBookId(bookId);
// 		doNothing().when(bookTagRepository).deleteByBookId(bookId);
// 		doNothing().when(bookContributorRepository).deleteByBookId(bookId);
//
// 		// when
// 		bookService.deleteBook(bookId);
//
// 		// then
// 		verify(bookRepository, times(1)).findById(bookId);
// 		verify(bookRepository, times(1)).delete(book);
// 		verify(bookCategoryRepository, times(1)).deleteByBookId(bookId);
// 		verify(bookTagRepository, times(1)).deleteByBookId(bookId);
// 		verify(bookContributorRepository, times(1)).deleteByBookId(bookId);
// 	}
//
// 	@Test
// 	@DisplayName("도서 삭제 실패 - 존재하지 않는 도서")
// 	void deleteBook_Fail_BookNotFound() {
// 		// given
// 		Long bookId = 999L;
// 		given(bookRepository.findById(bookId)).willReturn(Optional.empty());
//
// 		// when & then
// 		assertThatThrownBy(() -> bookService.deleteBook(bookId))
// 			.isInstanceOf(BookNotFoundException.class)
// 			.hasMessageContaining("존재하지 않는 도서입니다");
//
// 		verify(bookRepository, times(1)).findById(bookId);
// 		verify(bookRepository, never()).delete(any(Book.class));
// 	}
//
// 	@Test
// 	@DisplayName("도서 목록 조회 성공 - 빈 목록")
// 	void getBooks_Success_EmptyList() {
// 		// given
// 		Pageable pageable = PageRequest.of(0, 10);
// 		given(bookRepository.findAll()).willReturn(List.of());
//
// 		// when
// 		List<BookResponse> result = bookService.getBooks(pageable);
//
// 		// then
// 		assertThat(result).isNotNull();
// 		assertThat(result).isEmpty();
// 		verify(bookRepository, times(1)).findAll();
// 	}
//
// 	@Test
// 	@DisplayName("도서 재고 조회 성공")
// 	void getBookStock_Success() {
// 		// given
// 		Long bookId = 1L;
// 		Book book = new Book();
// 		ReflectionTestUtils.setField(book, "bookId", bookId);
// 		ReflectionTestUtils.setField(book, "bookName", "테스트 도서");
// 		ReflectionTestUtils.setField(book, "stock", 10);
//
// 		given(bookRepository.findById(bookId)).willReturn(Optional.of(book));
//
// 		// when
// 		BookStockResponse result = bookService.getBookStock(bookId);
//
// 		// then
// 		assertThat(result).isNotNull();
// 		assertThat(result.bookId()).isEqualTo(bookId);
// 		assertThat(result.bookName()).isEqualTo("테스트 도서");
// 		assertThat(result.stock()).isEqualTo(10);
// 		verify(bookRepository, times(1)).findById(bookId);
// 	}
//
// 	@Test
// 	@DisplayName("도서 재고 수정 성공")
// 	void updateBookStock_Success() {
// 		// given
// 		Long bookId = 1L;
// 		BookStockUpdateRequest request = new BookStockUpdateRequest(20);
// 		Book book = new Book();
// 		ReflectionTestUtils.setField(book, "bookId", bookId);
//
// 		given(bookRepository.findById(bookId)).willReturn(Optional.of(book));
//
// 		// when
// 		bookService.updateBookStock(bookId, request);
//
// 		// then
// 		assertThat(book.getStock()).isEqualTo(20);
// 		verify(bookRepository, times(1)).findById(bookId);
// 	}
// }
//
