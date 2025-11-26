package com.nhnacademy.byeol23backend.bookset.tag.repository;

import static org.assertj.core.api.Assertions.*;

import java.util.List;
import java.util.Optional;

import com.nhnacademy.byeol23backend.bookset.tag.domain.Tag;
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
class TagRepositoryTest {

	@Autowired
	private TagRepository tagRepository;

	@Autowired
	private TestEntityManager entityManager;

	private Tag testTag;

	@BeforeEach
	void setUp() {
		// 기본 테스트용 태그 하나 저장
		Tag tag = new Tag("backend");
		testTag = entityManager.persist(tag);
		entityManager.flush();
		entityManager.clear();
	}

	@Test
	@DisplayName("save - 태그 저장 테스트")
	void saveTag() {
		// given
		Tag tag = new Tag("frontend");

		// when
		Tag savedTag = tagRepository.save(tag);
		entityManager.flush();
		entityManager.clear();

		// then
		assertThat(savedTag.getTagId()).isNotNull();
		assertThat(savedTag.getTagName()).isEqualTo("frontend");
	}

	@Test
	@DisplayName("findById - 태그 조회 테스트")
	void findById_WhenExists_ReturnsTag() {
		// when
		Optional<Tag> result = tagRepository.findById(testTag.getTagId());

		// then
		assertThat(result).isPresent();
		Tag foundTag = result.get();
		assertThat(foundTag.getTagId()).isEqualTo(testTag.getTagId());
		assertThat(foundTag.getTagName()).isEqualTo("backend");
	}

	@Test
	@DisplayName("findById - 존재하지 않는 태그 조회 시 빈 Optional 반환")
	void findById_WhenNotExists_ReturnsEmpty() {
		// when
		Optional<Tag> result = tagRepository.findById(999L);

		// then
		assertThat(result).isEmpty();
	}

	@Test
	@DisplayName("findTagByTagId - 태그 조회 성공")
	void findTagByTagId_WhenExists_ReturnsTag() {
		// when
		Optional<Tag> result = tagRepository.findTagByTagId(testTag.getTagId());

		// then
		assertThat(result).isPresent();
		Tag foundTag = result.get();
		assertThat(foundTag.getTagId()).isEqualTo(testTag.getTagId());
		assertThat(foundTag.getTagName()).isEqualTo("backend");
	}

	@Test
	@DisplayName("findTagByTagId - 존재하지 않는 태그 조회 시 빈 Optional 반환")
	void findTagByTagId_WhenNotExists_ReturnsEmpty() {
		// when
		Optional<Tag> result = tagRepository.findTagByTagId(999L);

		// then
		assertThat(result).isEmpty();
	}

	@Test
	@DisplayName("findByTagId - 태그 조회 성공")
	void findByTagId_WhenExists_ReturnsTag() {
		// when
		Optional<Tag> result = tagRepository.findByTagId(testTag.getTagId());

		// then
		assertThat(result).isPresent();
		Tag foundTag = result.get();
		assertThat(foundTag.getTagId()).isEqualTo(testTag.getTagId());
		assertThat(foundTag.getTagName()).isEqualTo("backend");
	}

	@Test
	@DisplayName("findByTagId - 존재하지 않는 태그 조회 시 빈 Optional 반환")
	void findByTagId_WhenNotExists_ReturnsEmpty() {
		// when
		Optional<Tag> result = tagRepository.findByTagId(999L);

		// then
		assertThat(result).isEmpty();
	}

	@Test
	@DisplayName("getTagByTagId - 태그 조회 성공 (null 허용 메서드)")
	void getTagByTagId_WhenExists_ReturnsTag() {
		// when
		Tag foundTag = tagRepository.getTagByTagId(testTag.getTagId());

		// then
		assertThat(foundTag).isNotNull();
		assertThat(foundTag.getTagId()).isEqualTo(testTag.getTagId());
		assertThat(foundTag.getTagName()).isEqualTo("backend");
	}

	@Test
	@DisplayName("getTagByTagId - 존재하지 않는 태그 조회 시 null 반환")
	void getTagByTagId_WhenNotExists_ReturnsNull() {
		// when
		Tag foundTag = tagRepository.getTagByTagId(999L);

		// then
		assertThat(foundTag).isNull();
	}

	@Test
	@DisplayName("findAll - 전체 태그 조회 테스트")
	void findAllTags() {
		// given
		Tag tag2 = new Tag("devops");
		entityManager.persist(tag2);
		entityManager.flush();
		entityManager.clear();

		// when
		List<Tag> result = tagRepository.findAll();

		// then
		assertThat(result).hasSizeGreaterThanOrEqualTo(2);
		assertThat(result)
			.extracting(Tag::getTagName)
			.contains("backend", "devops");
	}

	@Test
	@DisplayName("deleteTagByTagId - 태그 삭제 성공")
	void deleteTagByTagId_WhenExists_DeletesTag() {
		// given
		Long tagId = testTag.getTagId();

		// when
		tagRepository.deleteTagByTagId(tagId);
		entityManager.flush();
		entityManager.clear();

		// then
		Optional<Tag> result = tagRepository.findById(tagId);
		assertThat(result).isEmpty();
	}

	@Test
	@DisplayName("deleteTagByTagId - 존재하지 않는 태그 삭제 시 예외 없이 통과")
	void deleteTagByTagId_WhenNotExists_DoesNothing() {
		// when
		tagRepository.deleteTagByTagId(999L);
		entityManager.flush();
		entityManager.clear();

		// then
		// 예외가 발생하지 않았으면 성공. 추가로 count 검사도 가능
		long count = tagRepository.count();
		assertThat(count).isGreaterThanOrEqualTo(1); // 최소한 setUp()의 태그 1개는 존재
	}

	@Test
	@DisplayName("findTagByTagName - 동일한 이름의 태그 개수 반환")
	void findTagByTagName_WhenExists_ReturnsCount() {
		// given
		Tag tag1 = new Tag("backend");
		Tag tag2 = new Tag("backend");
		Tag tag3 = new Tag("frontend");

		entityManager.persist(tag1);
		entityManager.persist(tag2);
		entityManager.persist(tag3);
		entityManager.flush();
		entityManager.clear();

		// when
		Long count = tagRepository.findTagByTagName("backend");

		// then
		assertThat(count).isEqualTo(3L);
	}

	@Test
	@DisplayName("findTagByTagName - 존재하지 않는 이름은 0 반환")
	void findTagByTagName_WhenNotExists_ReturnsZero() {
		// given
		Tag tag1 = new Tag("backend");
		Tag tag2 = new Tag("frontend");
		entityManager.persist(tag1);
		entityManager.persist(tag2);
		entityManager.flush();
		entityManager.clear();

		// when
		Long count = tagRepository.findTagByTagName("devops");

		// then
		assertThat(count).isZero();
	}

}
