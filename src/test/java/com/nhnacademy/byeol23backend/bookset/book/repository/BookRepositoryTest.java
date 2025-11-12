// package com.nhnacademy.byeol23backend.bookset.book.repository;
//
// import static org.assertj.core.api.Assertions.*;
// import static org.mockito.ArgumentMatchers.*;
// import static org.mockito.BDDMockito.*;
//
// import java.math.BigDecimal;
// import java.time.LocalDate;
// import java.util.ArrayList;
// import java.util.List;
// import java.util.Optional;
//
// import org.junit.jupiter.api.BeforeEach;
// import org.junit.jupiter.api.DisplayName;
// import org.junit.jupiter.api.Test;
// import org.junit.jupiter.api.extension.ExtendWith;
// import org.mockito.Mock;
// import org.mockito.junit.jupiter.MockitoExtension;
//
// import com.nhnacademy.byeol23backend.bookset.book.domain.Book;
// import com.nhnacademy.byeol23backend.bookset.bookimage.domain.BookImage;
// import com.nhnacademy.byeol23backend.bookset.publisher.domain.Publisher;
//
// @ExtendWith(MockitoExtension.class)
// class BookRepositoryTest {
//
// 	@Mock
// 	private BookRepository bookRepository;
//
// 	private Book testBook;
// 	private Publisher testPublisher;
//
// 	@BeforeEach
// 	void setUp() {
// 		// 테스트용 데이터 준비
// 		testPublisher = new Publisher();
// 		testBook = new Book();
// 		testBook.createBook(
// 			new com.nhnacademy.byeol23backend.bookset.book.dto.BookCreateRequest(
// 				"도서명",
// 				"목차",
// 				"설명",
// 				new BigDecimal(10000),
// 				new BigDecimal(9000),
// 				"1234567890123",
// 				LocalDate.of(2024, 5, 15),
// 				false,
// 				"판매중",
// 				10,
// 				1L,
// 				List.of(),
// 				List.of()
// 			),
// 			testPublisher
// 		);
// 	}
//
// 	// ========== existsByIsbn 테스트 ==========
//
// 	@Test
// 	@DisplayName("existsByIsbn - ISBN이 존재하는 경우 true 반환")
// 	void existsByIsbn_WhenExists_ReturnsTrue() {
// 		// given
// 		given(bookRepository.existsByIsbn("1234567890123")).willReturn(true);
//
// 		// when
// 		boolean exists = bookRepository.existsByIsbn("1234567890123");
//
// 		// then
// 		assertThat(exists).isTrue();
// 		verify(bookRepository, times(1)).existsByIsbn("1234567890123");
// 	}
//
// 	@Test
// 	@DisplayName("existsByIsbn - ISBN이 존재하지 않는 경우 false 반환")
// 	void existsByIsbn_WhenNotExists_ReturnsFalse() {
// 		// given
// 		given(bookRepository.existsByIsbn("9999999999999")).willReturn(false);
//
// 		// when
// 		boolean exists = bookRepository.existsByIsbn("9999999999999");
//
// 		// then
// 		assertThat(exists).isFalse();
// 		verify(bookRepository, times(1)).existsByIsbn("9999999999999");
// 	}
//
// 	// ========== existsByIsbnAndBookIdNot 테스트 ==========
//
// 	@Test
// 	@DisplayName("existsByIsbnAndBookIdNot - 다른 도서의 ISBN이 존재하는 경우 true 반환")
// 	void existsByIsbnAndBookIdNot_WhenExists_ReturnsTrue() {
// 		// given
// 		String isbn = "1234567890123";
// 		Long excludeBookId = 1L;
// 		given(bookRepository.existsByIsbnAndBookIdNot(isbn, excludeBookId)).willReturn(true);
//
// 		// when
// 		boolean exists = bookRepository.existsByIsbnAndBookIdNot(isbn, excludeBookId);
//
// 		// then
// 		assertThat(exists).isTrue();
// 		verify(bookRepository, times(1)).existsByIsbnAndBookIdNot(isbn, excludeBookId);
// 	}
//
// 	@Test
// 	@DisplayName("existsByIsbnAndBookIdNot - 다른 도서의 ISBN이 존재하지 않는 경우 false 반환")
// 	void existsByIsbnAndBookIdNot_WhenNotExists_ReturnsFalse() {
// 		// given
// 		String isbn = "1234567890123";
// 		Long excludeBookId = 1L;
// 		given(bookRepository.existsByIsbnAndBookIdNot(isbn, excludeBookId)).willReturn(false);
//
// 		// when
// 		boolean exists = bookRepository.existsByIsbnAndBookIdNot(isbn, excludeBookId);
//
// 		// then
// 		assertThat(exists).isFalse();
// 		verify(bookRepository, times(1)).existsByIsbnAndBookIdNot(isbn, excludeBookId);
// 	}
//
// 	@Test
// 	@DisplayName("existsByIsbnAndBookIdNot - 자기 자신의 ISBN을 제외하면 false 반환")
// 	void existsByIsbnAndBookIdNot_WhenSelfBook_ReturnsFalse() {
// 		// given
// 		String isbn = "1234567890123";
// 		Long bookId = 1L;
// 		given(bookRepository.existsByIsbnAndBookIdNot(isbn, bookId)).willReturn(false);
//
// 		// when
// 		boolean exists = bookRepository.existsByIsbnAndBookIdNot(isbn, bookId);
//
// 		// then
// 		assertThat(exists).isFalse();
// 		verify(bookRepository, times(1)).existsByIsbnAndBookIdNot(isbn, bookId);
// 	}
//
// 	// ========== findBookWithImagesById 테스트 ==========
//
// 	@Test
// 	@DisplayName("findBookWithImagesById - 이미지와 함께 도서 조회 성공")
// 	void findBookWithImagesById_WhenExists_ReturnsBookWithImages() {
// 		// given
// 		Long bookId = 1L;
// 		Book bookWithImages = testBook;
// 		BookImage image1 = new BookImage(bookWithImages, "image1.jpg");
// 		BookImage image2 = new BookImage(bookWithImages, "image2.jpg");
// 		bookWithImages.getBookImageUrls().add(image1);
// 		bookWithImages.getBookImageUrls().add(image2);
//
// 		given(bookRepository.findBookWithImagesById(bookId)).willReturn(Optional.of(bookWithImages));
//
// 		// when
// 		Optional<Book> result = bookRepository.findBookWithImagesById(bookId);
//
// 		// then
// 		assertThat(result).isPresent();
// 		Book foundBook = result.get();
// 		assertThat(foundBook.getBookId()).isEqualTo(bookWithImages.getBookId());
// 		assertThat(foundBook.getBookImageUrls()).isNotEmpty();
// 		verify(bookRepository, times(1)).findBookWithImagesById(bookId);
// 	}
//
// 	@Test
// 	@DisplayName("findBookWithImagesById - 존재하지 않는 도서 조회 시 빈 Optional 반환")
// 	void findBookWithImagesById_WhenNotExists_ReturnsEmpty() {
// 		// given
// 		Long bookId = 999L;
// 		given(bookRepository.findBookWithImagesById(bookId)).willReturn(Optional.empty());
//
// 		// when
// 		Optional<Book> result = bookRepository.findBookWithImagesById(bookId);
//
// 		// then
// 		assertThat(result).isEmpty();
// 		verify(bookRepository, times(1)).findBookWithImagesById(bookId);
// 	}
//
// 	@Test
// 	@DisplayName("findBookWithImagesById - 이미지가 없는 도서 조회")
// 	void findBookWithImagesById_WhenNoImages_ReturnsBookWithoutImages() {
// 		// given
// 		Long bookId = 1L;
// 		Book bookWithoutImages = testBook;
// 		given(bookRepository.findBookWithImagesById(bookId)).willReturn(Optional.of(bookWithoutImages));
//
// 		// when
// 		Optional<Book> result = bookRepository.findBookWithImagesById(bookId);
//
// 		// then
// 		assertThat(result).isPresent();
// 		assertThat(result.get().getBookId()).isEqualTo(bookWithoutImages.getBookId());
// 		verify(bookRepository, times(1)).findBookWithImagesById(bookId);
// 	}
//
// 	// ========== 기본 CRUD 테스트 ==========
//
// 	@Test
// 	@DisplayName("save - 도서 저장 테스트")
// 	void saveBook() {
// 		// given
// 		Book newBook = testBook;
// 		given(bookRepository.save(any(Book.class))).willReturn(newBook);
//
// 		// when
// 		Book savedBook = bookRepository.save(newBook);
//
// 		// then
// 		assertThat(savedBook).isNotNull();
// 		assertThat(savedBook.getBookName()).isEqualTo("도서명");
// 		verify(bookRepository, times(1)).save(newBook);
// 	}
//
// 	@Test
// 	@DisplayName("findById - 도서 조회 테스트")
// 	void findById() {
// 		// given
// 		Long bookId = 1L;
// 		given(bookRepository.findById(bookId)).willReturn(Optional.of(testBook));
//
// 		// when
// 		Optional<Book> result = bookRepository.findById(bookId);
//
// 		// then
// 		assertThat(result).isPresent();
// 		assertThat(result.get().getBookName()).isEqualTo("도서명");
// 		assertThat(result.get().getIsbn()).isEqualTo("1234567890123");
// 		verify(bookRepository, times(1)).findById(bookId);
// 	}
//
// 	@Test
// 	@DisplayName("findById - 존재하지 않는 도서 조회 시 빈 Optional 반환")
// 	void findById_WhenNotExists_ReturnsEmpty() {
// 		// given
// 		Long bookId = 999L;
// 		given(bookRepository.findById(bookId)).willReturn(Optional.empty());
//
// 		// when
// 		Optional<Book> result = bookRepository.findById(bookId);
//
// 		// then
// 		assertThat(result).isEmpty();
// 		verify(bookRepository, times(1)).findById(bookId);
// 	}
//
// 	@Test
// 	@DisplayName("findAll - 도서 전체 조회 테스트")
// 	void findAllBooks() {
// 		// given
// 		Book book1 = testBook;
// 		Book book2 = new Book();
// 		book2.createBook(
// 			new com.nhnacademy.byeol23backend.bookset.book.dto.BookCreateRequest(
// 				"도서2",
// 				"목차2",
// 				"설명2",
// 				new BigDecimal(20000),
// 				new BigDecimal(18000),
// 				"9876543210987",
// 				LocalDate.of(2024, 6, 1),
// 				false,
// 				"판매중",
// 				5,
// 				1L,
// 				List.of(),
// 				List.of()
// 			),
// 			testPublisher
// 		);
//
// 		List<Book> books = List.of(book1, book2);
// 		given(bookRepository.findAll()).willReturn(books);
//
// 		// when
// 		List<Book> result = bookRepository.findAll();
//
// 		// then
// 		assertThat(result).hasSize(2);
// 		assertThat(result).extracting(Book::getBookName)
// 			.containsExactly("도서명", "도서2");
// 		verify(bookRepository, times(1)).findAll();
// 	}
//
// 	@Test
// 	@DisplayName("findAll - 빈 목록 조회 테스트")
// 	void findAll_WhenEmpty_ReturnsEmptyList() {
// 		// given
// 		given(bookRepository.findAll()).willReturn(new ArrayList<>());
//
// 		// when
// 		List<Book> result = bookRepository.findAll();
//
// 		// then
// 		assertThat(result).isEmpty();
// 		verify(bookRepository, times(1)).findAll();
// 	}
//
// 	@Test
// 	@DisplayName("delete - 도서 삭제 테스트")
// 	void deleteBook() {
// 		// given
// 		Book bookToDelete = testBook;
// 		doNothing().when(bookRepository).delete(any(Book.class));
//
// 		// when
// 		bookRepository.delete(bookToDelete);
//
// 		// then
// 		verify(bookRepository, times(1)).delete(bookToDelete);
// 	}
//
// 	@Test
// 	@DisplayName("saveAll - 여러 도서 저장 테스트")
// 	void saveAllBooks() {
// 		// given
// 		Book book1 = testBook;
// 		Book book2 = new Book();
// 		book2.createBook(
// 			new com.nhnacademy.byeol23backend.bookset.book.dto.BookCreateRequest(
// 				"도서2",
// 				"목차2",
// 				"설명2",
// 				new BigDecimal(20000),
// 				new BigDecimal(18000),
// 				"9876543210987",
// 				LocalDate.of(2024, 6, 1),
// 				false,
// 				"판매중",
// 				5,
// 				1L,
// 				List.of(),
// 				List.of()
// 			),
// 			testPublisher
// 		);
//
// 		List<Book> booksToSave = List.of(book1, book2);
// 		given(bookRepository.saveAll(anyList())).willReturn(booksToSave);
//
// 		// when
// 		List<Book> savedBooks = bookRepository.saveAll(booksToSave);
//
// 		// then
// 		assertThat(savedBooks).hasSize(2);
// 		verify(bookRepository, times(1)).saveAll(booksToSave);
// 	}
// }
