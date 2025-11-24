package com.nhnacademy.byeol23backend.bookset.bookTag.repository;

import static org.assertj.core.api.Assertions.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.cloud.netflix.eureka.EurekaClientAutoConfiguration;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;

import com.nhnacademy.byeol23backend.bookset.book.domain.Book;
import com.nhnacademy.byeol23backend.bookset.book.domain.BookStatus;
import com.nhnacademy.byeol23backend.bookset.book.dto.BookCreateRequest;
import com.nhnacademy.byeol23backend.bookset.booktag.domain.BookTag;
import com.nhnacademy.byeol23backend.bookset.booktag.repository.BookTagRepository;
import com.nhnacademy.byeol23backend.bookset.publisher.domain.Publisher;
import com.nhnacademy.byeol23backend.bookset.tag.domain.Tag;
import com.nhnacademy.byeol23backend.config.QueryDslConfig;

@ActiveProfiles("test")
@DataJpaTest(excludeAutoConfiguration = {EurekaClientAutoConfiguration.class})
@Import(QueryDslConfig.class)
class BookTagRepositoryTest {

	@Autowired
	private BookTagRepository bookTagRepository;

	@Autowired
	private TestEntityManager entityManager;

	private Publisher testPublisher;
	private Book testBook;
	private Tag testTag1;
	private Tag testTag2;
	private BookTag testBookTag;

	@BeforeEach
	void setUp() {
		// 테스트용 출판사 생성
		testPublisher = new Publisher("민음사");
		testPublisher = entityManager.persist(testPublisher);
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

		// 테스트용 태그 생성
		testTag1 = new Tag("노벨문학상");
		testTag1 = entityManager.persist(testTag1);
		entityManager.flush();

		testTag2 = new Tag("베스트셀러");
		testTag2 = entityManager.persist(testTag2);
		entityManager.flush();

		// 테스트용 BookTag 생성
		testBookTag = BookTag.of(testBook, testTag1);
		testBookTag = bookTagRepository.save(testBookTag);
		entityManager.flush();
		entityManager.clear();
	}

	@Test
	@DisplayName("findTagsByBookId - 도서 ID로 태그 조회 성공")
	void findTagsByBookId_WhenExists_ReturnsTags() {
		// when
		List<Tag> result = bookTagRepository.findTagsByBookId(testBook.getBookId());

		// then
		assertThat(result).isNotNull();
		assertThat(result).hasSize(1);
		assertThat(result.get(0).getTagId()).isEqualTo(testTag1.getTagId());
		assertThat(result.get(0).getTagName()).isEqualTo("노벨문학상");
	}

	@Test
	@DisplayName("findTagsByBookId - 태그가 없는 경우 빈 리스트 반환")
	void findTagsByBookId_WhenNoTags_ReturnsEmptyList() {
		// given
		BookCreateRequest createRequest2 = new BookCreateRequest(
			"태그 없는 도서",
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
		book2 = entityManager.persist(book2);
		entityManager.flush();
		entityManager.clear();

		// when
		List<Tag> result = bookTagRepository.findTagsByBookId(book2.getBookId());

		// then
		assertThat(result).isNotNull();
		assertThat(result).isEmpty();
	}

	@Test
	@DisplayName("findTagsByBookId - 여러 태그가 있는 경우 모두 조회")
	void findTagsByBookId_WhenMultipleTags_ReturnsAllTags() {
		// given
		BookTag bookTag2 = BookTag.of(testBook, testTag2);
		bookTagRepository.save(bookTag2);
		entityManager.flush();
		entityManager.clear();

		// when
		List<Tag> result = bookTagRepository.findTagsByBookId(testBook.getBookId());

		// then
		assertThat(result).isNotNull();
		assertThat(result).hasSize(2);
		assertThat(result).extracting(Tag::getTagName)
			.containsExactlyInAnyOrder("노벨문학상", "베스트셀러");
	}

	@Test
	@DisplayName("findByBookIdsWithTag - 여러 도서 ID로 태그와 함께 조회 성공")
	void findByBookIdsWithTag_WhenExists_ReturnsBookTagsWithTags() {
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
		book2 = entityManager.persist(book2);
		entityManager.flush();

		BookTag bookTag2 = BookTag.of(book2, testTag2);
		bookTagRepository.save(bookTag2);
		entityManager.flush();
		entityManager.clear();

		// when
		List<BookTag> result = bookTagRepository.findByBookIdsWithTag(
			List.of(testBook.getBookId(), book2.getBookId()));

		// then
		assertThat(result).isNotNull();
		assertThat(result).hasSize(2);
		assertThat(result).extracting(bt -> bt.getTag().getTagName())
			.containsExactlyInAnyOrder("노벨문학상", "베스트셀러");
	}

	@Test
	@DisplayName("deleteByBookIdAndTagIds - 특정 태그 삭제 성공")
	void deleteByBookIdAndTagIds_Success() {
		// given
		BookTag bookTag2 = BookTag.of(testBook, testTag2);
		bookTagRepository.save(bookTag2);
		entityManager.flush();
		entityManager.clear();

		// when
		bookTagRepository.deleteByBookIdAndTagIds(testBook.getBookId(), List.of(testTag1.getTagId()));
		entityManager.flush();
		entityManager.clear();

		// then
		List<Tag> remainingTags = bookTagRepository.findTagsByBookId(testBook.getBookId());
		assertThat(remainingTags).hasSize(1);
		assertThat(remainingTags.get(0).getTagId()).isEqualTo(testTag2.getTagId());
	}

	@Test
	@DisplayName("deleteByBookIdAndTagIds - 여러 태그 삭제 성공")
	void deleteByBookIdAndTagIds_MultipleTags_Success() {
		// given
		BookTag bookTag2 = BookTag.of(testBook, testTag2);
		bookTagRepository.save(bookTag2);
		entityManager.flush();
		entityManager.clear();

		// when
		bookTagRepository.deleteByBookIdAndTagIds(
			testBook.getBookId(),
			List.of(testTag1.getTagId(), testTag2.getTagId())
		);
		entityManager.flush();
		entityManager.clear();

		// then
		List<Tag> remainingTags = bookTagRepository.findTagsByBookId(testBook.getBookId());
		assertThat(remainingTags).isEmpty();
	}

	@Test
	@DisplayName("deleteByBookId - 도서 ID로 모든 태그 삭제 성공")
	void deleteByBookId_Success() {
		// given
		BookTag bookTag2 = BookTag.of(testBook, testTag2);
		bookTagRepository.save(bookTag2);
		entityManager.flush();
		entityManager.clear();

		// when
		bookTagRepository.deleteByBookId(testBook.getBookId());
		entityManager.flush();
		entityManager.clear();

		// then
		List<Tag> remainingTags = bookTagRepository.findTagsByBookId(testBook.getBookId());
		assertThat(remainingTags).isEmpty();
	}

	@Test
	@DisplayName("save - BookTag 저장 테스트")
	void saveBookTag() {
		// given
		BookTag newBookTag = BookTag.of(testBook, testTag2);

		// when
		BookTag savedBookTag = bookTagRepository.save(newBookTag);
		entityManager.flush();
		entityManager.clear();

		// then
		assertThat(savedBookTag.getBookTagId()).isNotNull();
		assertThat(savedBookTag.getBook().getBookId()).isEqualTo(testBook.getBookId());
		assertThat(savedBookTag.getTag().getTagId()).isEqualTo(testTag2.getTagId());
	}

	@Test
	@DisplayName("findById - BookTag 조회 테스트")
	void findById() {
		// when
		var result = bookTagRepository.findById(testBookTag.getBookTagId());

		// then
		assertThat(result).isPresent();
		BookTag foundBookTag = result.get();
		assertThat(foundBookTag.getBook().getBookId()).isEqualTo(testBook.getBookId());
		assertThat(foundBookTag.getTag().getTagId()).isEqualTo(testTag1.getTagId());
	}

	@Test
	@DisplayName("findAll - BookTag 전체 조회 테스트")
	void findAllBookTags() {
		// given
		BookTag bookTag2 = BookTag.of(testBook, testTag2);
		bookTagRepository.save(bookTag2);
		entityManager.flush();
		entityManager.clear();

		// when
		List<BookTag> result = bookTagRepository.findAll();

		// then
		assertThat(result).hasSizeGreaterThanOrEqualTo(2);
	}
}
