package com.nhnacademy.byeol23backend.bookset.publisher.repository;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import java.util.Optional;

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

@ActiveProfiles("test")
@DataJpaTest(excludeAutoConfiguration = {EurekaClientAutoConfiguration.class})
@Import(QueryDslConfig.class)
class PublisherRepositoryTest {

	@Autowired
	private PublisherRepository publisherRepository;

	@Autowired
	private TestEntityManager entityManager;

	private Publisher testPublisher;

	@BeforeEach
	void setUp() {
		// 기본 테스트용 출판사 하나 저장
		Publisher publisher = new Publisher("민음사");
		testPublisher = entityManager.persist(publisher);
		entityManager.flush();
		entityManager.clear();
	}

	@Test
	@DisplayName("save - 출판사 저장 테스트")
	void savePublisher() {
		// given
		Publisher publisher = new Publisher("문학동네");

		// when
		Publisher saved = publisherRepository.save(publisher);
		entityManager.flush();
		entityManager.clear();

		// then
		assertThat(saved.getPublisherId()).isNotNull();
		assertThat(saved.getPublisherName()).isEqualTo("문학동네");
	}

	@Test
	@DisplayName("findById - 출판사 조회 (존재할 때)")
	void findById_WhenExists_ReturnsPublisher() {
		// when
		Optional<Publisher> result = publisherRepository.findById(testPublisher.getPublisherId());

		// then
		assertThat(result).isPresent();
		Publisher found = result.get();
		assertThat(found.getPublisherId()).isEqualTo(testPublisher.getPublisherId());
		assertThat(found.getPublisherName()).isEqualTo("민음사");
	}

	@Test
	@DisplayName("findById - 출판사 조회 (없을 때) 빈 Optional")
	void findById_WhenNotExists_ReturnsEmpty() {
		// when
		Optional<Publisher> result = publisherRepository.findById(999L);

		// then
		assertThat(result).isEmpty();
	}

	@Test
	@DisplayName("getPublisherByPublisherId - null 허용 메서드 (존재할 때)")
	void getPublisherByPublisherId_WhenExists_ReturnsPublisher() {
		// when
		Publisher found = publisherRepository.getPublisherByPublisherId(testPublisher.getPublisherId());

		// then
		assertThat(found).isNotNull();
		assertThat(found.getPublisherId()).isEqualTo(testPublisher.getPublisherId());
		assertThat(found.getPublisherName()).isEqualTo("민음사");
	}

	@Test
	@DisplayName("getPublisherByPublisherId - 존재하지 않을 때 null 반환")
	void getPublisherByPublisherId_WhenNotExists_ReturnsNull() {
		// when
		Publisher found = publisherRepository.getPublisherByPublisherId(999L);

		// then
		assertThat(found).isNull();
	}

	@Test
	@DisplayName("findPublisherByPublisherId - 존재할 때 Publisher 반환")
	void findPublisherByPublisherId_WhenExists_ReturnsPublisher() {
		// when
		Publisher found = publisherRepository.findPublisherByPublisherId(testPublisher.getPublisherId());

		// then
		assertThat(found).isNotNull();
		assertThat(found.getPublisherId()).isEqualTo(testPublisher.getPublisherId());
		assertThat(found.getPublisherName()).isEqualTo("민음사");
	}

	@Test
	@DisplayName("findPublisherByPublisherId - 존재하지 않을 때 null 반환")
	void findPublisherByPublisherId_WhenNotExists_ReturnsNull() {
		// when
		Publisher found = publisherRepository.findPublisherByPublisherId(999L);

		// then
		assertThat(found).isNull();
	}

	@Test
	@DisplayName("findByPublisherId - Optional 조회 (존재할 때)")
	void findByPublisherId_WhenExists_ReturnsOptionalPublisher() {
		// when
		Optional<Publisher> result = publisherRepository.findByPublisherId(testPublisher.getPublisherId());

		// then
		assertThat(result).isPresent();
		Publisher found = result.get();
		assertThat(found.getPublisherId()).isEqualTo(testPublisher.getPublisherId());
		assertThat(found.getPublisherName()).isEqualTo("민음사");
	}

	@Test
	@DisplayName("findByPublisherId - Optional 조회 (없을 때) 빈 Optional")
	void findByPublisherId_WhenNotExists_ReturnsEmpty() {
		// when
		Optional<Publisher> result = publisherRepository.findByPublisherId(999L);

		// then
		assertThat(result).isEmpty();
	}

	@Test
	@DisplayName("findByPublisherName - 이름으로 조회 (존재할 때)")
	void findByPublisherName_WhenExists_ReturnsOptionalPublisher() {
		// when
		Optional<Publisher> result = publisherRepository.findByPublisherName("민음사");

		// then
		assertThat(result).isPresent();
		Publisher found = result.get();
		assertThat(found.getPublisherName()).isEqualTo("민음사");
	}

	@Test
	@DisplayName("findByPublisherName - 이름으로 조회 (없을 때) 빈 Optional")
	void findByPublisherName_WhenNotExists_ReturnsEmpty() {
		// when
		Optional<Publisher> result = publisherRepository.findByPublisherName("없는출판사");

		// then
		assertThat(result).isEmpty();
	}

	@Test
	@DisplayName("findAll - 전체 출판사 조회")
	void findAllPublishers() {
		// given
		Publisher publisher2 = new Publisher("문학동네");
		entityManager.persist(publisher2);
		entityManager.flush();
		entityManager.clear();

		// when
		List<Publisher> result = publisherRepository.findAll();

		// then
		assertThat(result).hasSizeGreaterThanOrEqualTo(2);
		assertThat(result)
			.extracting(Publisher::getPublisherName)
			.contains("민음사", "문학동네");
	}

	@Test
	@DisplayName("deletePublisherByPublisherId - 존재하는 출판사 삭제 성공")
	void deletePublisherByPublisherId_WhenExists() {
		// given
		Long id = testPublisher.getPublisherId();

		// when
		publisherRepository.deletePublisherByPublisherId(id);
		entityManager.flush();
		entityManager.clear();

		// then
		Optional<Publisher> result = publisherRepository.findById(id);
		assertThat(result).isEmpty();
	}

	@Test
	@DisplayName("deletePublisherByPublisherId - 존재하지 않는 출판사 삭제 시 예외 없이 통과")
	void deletePublisherByPublisherId_WhenNotExists_DoesNothing() {
		// when
		publisherRepository.deletePublisherByPublisherId(999L);
		entityManager.flush();
		entityManager.clear();

		// then
		long count = publisherRepository.count();
		// setUp()에서 1개 이상 저장되어 있음
		assertThat(count).isGreaterThanOrEqualTo(1);
	}
}
