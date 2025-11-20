package com.nhnacademy.byeol23backend.bookset.book.repository;

import static org.assertj.core.api.Assertions.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.cloud.netflix.eureka.EurekaClientAutoConfiguration;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.ActiveProfiles;

import com.nhnacademy.byeol23backend.bookset.book.domain.Book;
import com.nhnacademy.byeol23backend.bookset.book.domain.BookStatus;
import com.nhnacademy.byeol23backend.bookset.book.dto.BookCreateRequest;
import com.nhnacademy.byeol23backend.bookset.bookimage.domain.BookImage;
import com.nhnacademy.byeol23backend.bookset.publisher.domain.Publisher;
import com.nhnacademy.byeol23backend.config.QueryDslConfig;

@ActiveProfiles("test")
@DataJpaTest(excludeAutoConfiguration = {EurekaClientAutoConfiguration.class})
@Import(QueryDslConfig.class)
class BookRepositoryTest {

	@Autowired
	private BookRepository bookRepository;

	@Autowired
	private TestEntityManager entityManager;

	private Publisher testPublisher;
	private Book testBook;

	@BeforeEach
	void setUp() {
		// 테스트용 출판사 생성
		testPublisher = new Publisher("민음사");
		testPublisher = entityManager.merge(testPublisher);
		entityManager.flush();

		// 테스트용 도서 생성
		BookCreateRequest createRequest = new BookCreateRequest(
			"도서명",
			"목차",
			"설명",
			new BigDecimal(10000),
			new BigDecimal(9000),
			"1234567890123",
			LocalDate.of(2024, 5, 15),
			false,
			BookStatus.SALE,
			10,
			testPublisher.getPublisherId(),
			List.of(),
			List.of(),
			List.of(),
			List.of(),
			null
		);

		testBook = new Book();
		testBook.createBook(createRequest, testPublisher);
		testBook = bookRepository.save(testBook);
		entityManager.flush();
		entityManager.clear();
	}

	@Test
	@DisplayName("existsByIsbn - ISBN이 존재하는 경우 true 반환")
	void existsByIsbn_WhenExists_ReturnsTrue() {
		// when
		boolean exists = bookRepository.existsByIsbn("1234567890123");

		// then
		assertThat(exists).isTrue();
	}

	@Test
	@DisplayName("existsByIsbn - ISBN이 존재하지 않는 경우 false 반환")
	void existsByIsbn_WhenNotExists_ReturnsFalse() {
		// when
		boolean exists = bookRepository.existsByIsbn("9999999999999");

		// then
		assertThat(exists).isFalse();
	}

	@Test
	@DisplayName("findBookWithImagesById - 이미지와 함께 도서 조회 성공")
	void findBookWithImagesById_WhenExists_ReturnsBookWithImages() {
		// given
		BookImage image1 = new BookImage(testBook, "image1.jpg");
		BookImage image2 = new BookImage(testBook, "image2.jpg");
		entityManager.persistAndFlush(image1);
		entityManager.persistAndFlush(image2);
		entityManager.clear();

		// when
		Optional<Book> result = bookRepository.findBookWithImagesById(testBook.getBookId());

		// then
		assertThat(result).isPresent();
		Book foundBook = result.get();
		assertThat(foundBook.getBookId()).isEqualTo(testBook.getBookId());
		assertThat(foundBook.getBookImageUrls()).isNotEmpty();
		assertThat(foundBook.getBookImageUrls()).hasSize(2);
	}

	@Test
	@DisplayName("findBookWithImagesById - 존재하지 않는 도서 조회 시 빈 Optional 반환")
	void findBookWithImagesById_WhenNotExists_ReturnsEmpty() {
		// when
		Optional<Book> result = bookRepository.findBookWithImagesById(999L);

		// then
		assertThat(result).isEmpty();
	}

	@Test
	@DisplayName("queryBookWithPublisherById - 출판사와 함께 도서 조회 성공")
	void queryBookWithPublisherById_WhenExists_ReturnsBookWithPublisher() {
		// when
		Book result = bookRepository.queryBookWithPublisherById(testBook.getBookId());

		// then
		assertThat(result).isNotNull();
		assertThat(result.getBookId()).isEqualTo(testBook.getBookId());
		assertThat(result.getPublisher()).isNotNull();
		assertThat(result.getPublisher().getPublisherId()).isEqualTo(testPublisher.getPublisherId());
		assertThat(result.getPublisher().getPublisherName()).isEqualTo("민음사");
	}

	@Test
	@DisplayName("decreaseBookStock - 재고 감소 성공")
	void decreaseBookStock_Success() {
		// given
		int initialStock = testBook.getStock();
		int quantity = 5;

		// when
		int updatedRows = bookRepository.decreaseBookStock(testBook.getBookId(), quantity);
		entityManager.clear();

		// then
		assertThat(updatedRows).isEqualTo(1);
		Book updatedBook = bookRepository.findById(testBook.getBookId()).orElseThrow();
		assertThat(updatedBook.getStock()).isEqualTo(initialStock - quantity);
	}

	@Test
	@DisplayName("decreaseBookStock - 재고가 부족한 경우 업데이트 실패")
	void decreaseBookStock_WhenInsufficientStock_ReturnsZero() {
		// given
		int quantity = 100; // 현재 재고보다 많은 수량

		// when
		int updatedRows = bookRepository.decreaseBookStock(testBook.getBookId(), quantity);

		// then
		assertThat(updatedRows).isEqualTo(0);
		Book book = bookRepository.findById(testBook.getBookId()).orElseThrow();
		assertThat(book.getStock()).isEqualTo(10); // 재고가 변경되지 않음
	}

	@Test
	@DisplayName("save - 도서 저장 테스트")
	void saveBook() {
		// given
		Publisher newPublisher = new Publisher("새 출판사");
		newPublisher = entityManager.merge(newPublisher);
		entityManager.flush();

		BookCreateRequest createRequest = new BookCreateRequest(
			"새 도서",
			"새 목차",
			"새 설명",
			new BigDecimal(20000),
			new BigDecimal(18000),
			"9876543210987",
			LocalDate.of(2024, 6, 1),
			false,
			BookStatus.SALE,
			5,
			newPublisher.getPublisherId(),
			List.of(),
			List.of(),
			List.of(),
			List.of(),
			null
		);

		Book newBook = new Book();
		newBook.createBook(createRequest, newPublisher);

		// when
		Book savedBook = bookRepository.save(newBook);
		entityManager.flush();
		entityManager.clear();

		// then
		assertThat(savedBook.getBookId()).isNotNull();
		assertThat(savedBook.getBookName()).isEqualTo("새 도서");
		assertThat(savedBook.getIsbn()).isEqualTo("9876543210987");
		assertThat(savedBook.getPublisher().getPublisherId()).isEqualTo(newPublisher.getPublisherId());
	}

	@Test
	@DisplayName("findById - 도서 조회 테스트")
	void findById() {
		// when
		Optional<Book> result = bookRepository.findById(testBook.getBookId());

		// then
		assertThat(result).isPresent();
		Book foundBook = result.get();
		assertThat(foundBook.getBookName()).isEqualTo("도서명");
		assertThat(foundBook.getIsbn()).isEqualTo("1234567890123");
		assertThat(foundBook.getRegularPrice()).isEqualByComparingTo(new BigDecimal(10000));
		assertThat(foundBook.getSalePrice()).isEqualByComparingTo(new BigDecimal(9000));
	}

	@Test
	@DisplayName("findById - 존재하지 않는 도서 조회 시 빈 Optional 반환")
	void findById_WhenNotExists_ReturnsEmpty() {
		// when
		Optional<Book> result = bookRepository.findById(999L);

		// then
		assertThat(result).isEmpty();
	}

	@Test
	@DisplayName("findAll - 도서 전체 조회 테스트")
	void findAllBooks() {
		// given
		Publisher publisher2 = new Publisher("출판사2");
		publisher2 = entityManager.merge(publisher2);
		entityManager.flush();

		BookCreateRequest createRequest2 = new BookCreateRequest(
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
			publisher2.getPublisherId(),
			List.of(),
			List.of(),
			List.of(),
			List.of(),
			null
		);

		Book book2 = new Book();
		book2.createBook(createRequest2, publisher2);
		bookRepository.save(book2);
		entityManager.flush();
		entityManager.clear();

		// when
		List<Book> result = bookRepository.findAll();

		// then
		assertThat(result).hasSizeGreaterThanOrEqualTo(2);
		assertThat(result).extracting(Book::getBookName)
			.contains("도서명", "도서2");
	}

	@Test
	@DisplayName("findAll - 페이징 조회 테스트")
	void findAll_WithPaging() {
		// given
		Publisher publisher2 = new Publisher("출판사2");
		publisher2 = entityManager.merge(publisher2);
		entityManager.flush();

		BookCreateRequest createRequest2 = new BookCreateRequest(
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
			publisher2.getPublisherId(),
			List.of(),
			List.of(),
			List.of(),
			List.of(),
			null
		);

		Book book2 = new Book();
		book2.createBook(createRequest2, publisher2);
		bookRepository.save(book2);
		entityManager.flush();
		entityManager.clear();

		// when
		Pageable pageable = PageRequest.of(0, 10);
		Page<Book> result = bookRepository.findAll(pageable);

		// then
		assertThat(result.getContent()).hasSizeGreaterThanOrEqualTo(2);
		assertThat(result.getTotalElements()).isGreaterThanOrEqualTo(2);
		assertThat(result.getTotalPages()).isGreaterThanOrEqualTo(1);
	}

	@Test
	@DisplayName("delete - 도서 삭제 테스트 (soft delete)")
	void deleteBook() {
		// given
		Long bookId = testBook.getBookId();

		// when
		bookRepository.delete(testBook);
		entityManager.flush();
		entityManager.clear();

		// then
		Optional<Book> result = bookRepository.findById(bookId);
		assertThat(result).isEmpty();
	}
}
