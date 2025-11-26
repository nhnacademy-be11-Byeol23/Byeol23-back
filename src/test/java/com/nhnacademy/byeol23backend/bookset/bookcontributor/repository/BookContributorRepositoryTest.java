package com.nhnacademy.byeol23backend.bookset.bookcontributor.repository;

import com.nhnacademy.byeol23backend.bookset.book.domain.Book;
import com.nhnacademy.byeol23backend.bookset.book.domain.BookStatus;
import com.nhnacademy.byeol23backend.bookset.book.dto.BookCreateRequest;
import com.nhnacademy.byeol23backend.bookset.bookcontributor.domain.BookContributor;
import com.nhnacademy.byeol23backend.bookset.contributor.domain.Contributor;
import com.nhnacademy.byeol23backend.bookset.contributor.domain.ContributorRole;
import com.nhnacademy.byeol23backend.bookset.contributor.domain.dto.ContributorCreateRequest;
import com.nhnacademy.byeol23backend.bookset.publisher.domain.Publisher;
import com.nhnacademy.byeol23backend.config.QueryDslConfig;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.cloud.netflix.eureka.EurekaClientAutoConfiguration;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@ActiveProfiles("test")
@DataJpaTest(excludeAutoConfiguration = {EurekaClientAutoConfiguration.class})
@Import(QueryDslConfig.class)
class BookContributorRepositoryTest {

	@Autowired
	private BookContributorRepository bookContributorRepository;

	@Autowired
	private TestEntityManager entityManager;

	private Publisher testPublisher;
	private Book testBook;
	private Contributor testContributor1;
	private Contributor testContributor2;
	private BookContributor testBookContributor;

	@BeforeEach
	void setUp() {
		// 테스트용 출판사 생성
		testPublisher = new Publisher("민음사");
		testPublisher = entityManager.merge(testPublisher);
		entityManager.flush();

		// 테스트용 도서 생성
		BookCreateRequest createRequest = new BookCreateRequest(
			"테스트 도서",
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
			List.of()
		);

		testBook = new Book();
		testBook.createBook(createRequest, testPublisher);
		testBook = entityManager.persist(testBook);
		entityManager.flush();

		// 테스트용 기여자 생성

		testContributor1 = new Contributor(new ContributorCreateRequest("홍길동", ContributorRole.AUTHOR));
		testContributor1 = entityManager.persist(testContributor1);
		entityManager.flush();

		testContributor2 = new Contributor(new ContributorCreateRequest("김철수", ContributorRole.TRANSLATOR));
		testContributor2 = entityManager.persist(testContributor2);
		entityManager.flush();

		// 테스트용 BookContributor 생성
		testBookContributor = BookContributor.of(testBook, testContributor1);
		testBookContributor = bookContributorRepository.save(testBookContributor);
		entityManager.flush();
		entityManager.clear();
	}

	@Test
	@DisplayName("findContributorByBookId - 도서 ID로 기여자 조회 성공")
	void findContributorByBookId_WhenExists_ReturnsContributors() {
		// when
		List<Contributor> result = bookContributorRepository.findContributorByBookId(testBook.getBookId());

		// then
		assertThat(result).isNotNull();
		assertThat(result).hasSize(1);
		assertThat(result.get(0).getContributorId()).isEqualTo(testContributor1.getContributorId());
		assertThat(result.get(0).getContributorName()).isEqualTo("홍길동");
		assertThat(result.get(0).getContributorRole().getLabel()).isEqualTo(ContributorRole.AUTHOR.getLabel());
	}

	@Test
	@DisplayName("findContributorByBookId - 기여자가 없는 경우 빈 리스트 반환")
	void findContributorByBookId_WhenNoContributors_ReturnsEmptyList() {
		// given
		BookCreateRequest createRequest2 = new BookCreateRequest(
			"기여자 없는 도서",
			"목차",
			"설명",
			new BigDecimal(20000),
			new BigDecimal(18000),
			"9876543210987",
			LocalDate.of(2024, 6, 1),
			false,
			BookStatus.SALE,
			5,
			testPublisher.getPublisherId(),
			List.of(),
			List.of(),
			List.of(),
			List.of()
		);

		Book book2 = new Book();
		book2.createBook(createRequest2, testPublisher);
		book2 = entityManager.merge(book2);
		entityManager.flush();
		entityManager.clear();

		// when
		List<Contributor> result = bookContributorRepository.findContributorByBookId(book2.getBookId());

		// then
		assertThat(result).isNotNull();
		assertThat(result).isEmpty();
	}

	@Test
	@DisplayName("findContributorByBookId - 여러 기여자가 있는 경우 모두 조회")
	void findContributorByBookId_WhenMultipleContributors_ReturnsAllContributors() {
		// given
		BookContributor bookContributor2 = BookContributor.of(testBook, testContributor2);
		bookContributorRepository.save(bookContributor2);
		entityManager.flush();
		entityManager.clear();

		// when
		List<Contributor> result = bookContributorRepository.findContributorByBookId(testBook.getBookId());

		// then
		assertThat(result).isNotNull();
		assertThat(result).hasSize(2);
		assertThat(result).extracting(Contributor::getContributorName)
			.containsExactlyInAnyOrder("홍길동", "김철수");
	}

	@Test
	@DisplayName("findByBookIdsWithContributor - 여러 도서 ID로 기여자와 함께 조회 성공")
	void findByBookIdsWithContributor_WhenExists_ReturnsBookContributorsWithContributors() {
		// given
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
			testPublisher.getPublisherId(),
			List.of(),
			List.of(),
			List.of(),
			List.of()
		);

		Book book2 = new Book();
		book2.createBook(createRequest2, testPublisher);
		book2 = entityManager.merge(book2);
		entityManager.flush();

		BookContributor bookContributor2 = BookContributor.of(book2, testContributor2);
		bookContributorRepository.save(bookContributor2);
		entityManager.flush();
		entityManager.clear();

		// when
		List<BookContributor> result = bookContributorRepository.findByBookIdsWithContributor(
			List.of(testBook.getBookId(), book2.getBookId()));

		// then
		assertThat(result).isNotNull();
		assertThat(result).hasSize(2);
		assertThat(result).extracting(bc -> bc.getContributor().getContributorName())
			.containsExactlyInAnyOrder("홍길동", "김철수");
	}

	@Test
	@DisplayName("deleteByBookIdAndContributorIds - 특정 기여자 삭제 성공")
	void deleteByBookIdAndContributorIds_Success() {
		// given
		BookContributor bookContributor2 = BookContributor.of(testBook, testContributor2);
		bookContributorRepository.save(bookContributor2);
		entityManager.flush();
		entityManager.clear();

		// when
		bookContributorRepository.deleteByBookIdAndContributorIds(
			testBook.getBookId(),
			List.of(testContributor1.getContributorId())
		);
		entityManager.flush();
		entityManager.clear();

		// then
		List<Contributor> remainingContributors = bookContributorRepository.findContributorByBookId(
			testBook.getBookId());
		assertThat(remainingContributors).hasSize(1);
		assertThat(remainingContributors.get(0).getContributorId()).isEqualTo(testContributor2.getContributorId());
	}

	@Test
	@DisplayName("deleteByBookIdAndContributorIds - 여러 기여자 삭제 성공")
	void deleteByBookIdAndContributorIds_MultipleContributors_Success() {
		// given
		BookContributor bookContributor2 = BookContributor.of(testBook, testContributor2);
		bookContributorRepository.save(bookContributor2);
		entityManager.flush();
		entityManager.clear();

		// when
		bookContributorRepository.deleteByBookIdAndContributorIds(
			testBook.getBookId(),
			List.of(testContributor1.getContributorId(), testContributor2.getContributorId())
		);
		entityManager.flush();
		entityManager.clear();

		// then
		List<Contributor> remainingContributors = bookContributorRepository.findContributorByBookId(
			testBook.getBookId());
		assertThat(remainingContributors).isEmpty();
	}

	@Test
	@DisplayName("deleteByBookId - 도서 ID로 모든 기여자 삭제 성공")
	void deleteByBookId_Success() {
		// given
		BookContributor bookContributor2 = BookContributor.of(testBook, testContributor2);
		bookContributorRepository.save(bookContributor2);
		entityManager.flush();
		entityManager.clear();

		// when
		bookContributorRepository.deleteByBookId(testBook.getBookId());
		entityManager.flush();
		entityManager.clear();

		// then
		List<Contributor> remainingContributors = bookContributorRepository.findContributorByBookId(
			testBook.getBookId());
		assertThat(remainingContributors).isEmpty();
	}

	@Test
	@DisplayName("save - BookContributor 저장 테스트")
	void saveBookContributor() {
		// given
		BookContributor newBookContributor = BookContributor.of(testBook, testContributor2);

		// when
		BookContributor savedBookContributor = bookContributorRepository.save(newBookContributor);
		entityManager.flush();
		entityManager.clear();

		// then
		assertThat(savedBookContributor.getBookContributorId()).isNotNull();
		assertThat(savedBookContributor.getBook().getBookId()).isEqualTo(testBook.getBookId());
		assertThat(savedBookContributor.getContributor().getContributorId()).isEqualTo(
			testContributor2.getContributorId());
	}

	@Test
	@DisplayName("findById - BookContributor 조회 테스트")
	void findById() {
		// when
		var result = bookContributorRepository.findById(testBookContributor.getBookContributorId());

		// then
		assertThat(result).isPresent();
		BookContributor foundBookContributor = result.get();
		assertThat(foundBookContributor.getBook().getBookId()).isEqualTo(testBook.getBookId());
		assertThat(foundBookContributor.getContributor().getContributorId()).isEqualTo(
			testContributor1.getContributorId());
	}

	@Test
	@DisplayName("findAll - BookContributor 전체 조회 테스트")
	void findAllBookContributors() {
		// given
		BookContributor bookContributor2 = BookContributor.of(testBook, testContributor2);
		bookContributorRepository.save(bookContributor2);
		entityManager.flush();
		entityManager.clear();

		// when
		List<BookContributor> result = bookContributorRepository.findAll();

		// then
		assertThat(result).hasSizeGreaterThanOrEqualTo(2);
	}
}

