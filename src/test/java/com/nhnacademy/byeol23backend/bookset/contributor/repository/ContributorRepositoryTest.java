package com.nhnacademy.byeol23backend.bookset.contributor.repository;

import static org.assertj.core.api.Assertions.*;

import java.util.List;
import java.util.Optional;

import com.nhnacademy.byeol23backend.bookset.contributor.domain.Contributor;
import com.nhnacademy.byeol23backend.bookset.contributor.domain.ContributorRole;
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
class ContributorRepositoryTest {

	@Autowired
	private ContributorRepository contributorRepository;

	@Autowired
	private TestEntityManager entityManager;

	private Contributor baseContributor;

	@BeforeEach
	void setUp() {
		// 기본 테스트용 기여자 하나 저장
		// ⚠️ 엔티티 생성자는 프로젝트에 맞게 조정 (예: new Contributor("name", role) 이면 그걸 사용)
		baseContributor = new Contributor( null, "base-writer", ContributorRole.AUTHOR );
		baseContributor = entityManager.persist(baseContributor);
		entityManager.flush();
		entityManager.clear();
	}

	@Test
	@DisplayName("save - 기여자 저장 테스트")
	void saveContributor() {
		// given
		Contributor contributor = new Contributor(null, "new-writer", ContributorRole.TRANSLATOR);

		// when
		Contributor saved = contributorRepository.save(contributor);
		entityManager.flush();
		entityManager.clear();

		// then
		assertThat(saved.getContributorId()).isNotNull();
		assertThat(saved.getContributorName()).isEqualTo("new-writer");
		assertThat(saved.getContributorRole()).isEqualTo(ContributorRole.TRANSLATOR);
	}

	@Test
	@DisplayName("findById - 존재하는 기여자 조회 성공")
	void findById_WhenExists_ReturnsContributor() {
		// when
		Optional<Contributor> result = contributorRepository.findById(baseContributor.getContributorId());

		// then
		assertThat(result).isPresent();
		Contributor found = result.get();
		assertThat(found.getContributorId()).isEqualTo(baseContributor.getContributorId());
		assertThat(found.getContributorName()).isEqualTo("base-writer");
		assertThat(found.getContributorRole()).isEqualTo(ContributorRole.AUTHOR);
	}

	@Test
	@DisplayName("findById - 존재하지 않는 기여자 조회 시 빈 Optional 반환")
	void findById_WhenNotExists_ReturnsEmpty() {
		// when
		Optional<Contributor> result = contributorRepository.findById(999L);

		// then
		assertThat(result).isEmpty();
	}

	@Test
	@DisplayName("findAll - 전체 기여자 조회")
	void findAllContributors() {
		// given
		Contributor c2 = new Contributor(null, "another-writer", ContributorRole.TRANSLATOR);
		entityManager.persist(c2);
		entityManager.flush();
		entityManager.clear();

		// when
		List<Contributor> result = contributorRepository.findAll();

		// then
		assertThat(result).hasSizeGreaterThanOrEqualTo(2);
		assertThat(result)
			.extracting(Contributor::getContributorName)
			.contains("base-writer", "another-writer");
	}

	// ─────────────── findByContributorNameAndContributorRole ───────────────

	@Test
	@DisplayName("findByContributorNameAndContributorRole - 이름+역할로 조회 성공")
	void findByNameAndRole_WhenExists_ReturnsContributor() {
		// given
		Contributor another = new Contributor(null, "target-writer", ContributorRole.AUTHOR);
		entityManager.persist(another);
		entityManager.flush();
		entityManager.clear();

		// when
		Optional<Contributor> result = contributorRepository
			.findByContributorNameAndContributorRole("target-writer", ContributorRole.AUTHOR);

		// then
		assertThat(result).isPresent();
		Contributor found = result.get();
		assertThat(found.getContributorName()).isEqualTo("target-writer");
		assertThat(found.getContributorRole()).isEqualTo(ContributorRole.AUTHOR);
	}

	@Test
	@DisplayName("findByContributorNameAndContributorRole - 일치하는 데이터가 없으면 빈 Optional")
	void findByNameAndRole_WhenNotExists_ReturnsEmpty() {
		// when
		Optional<Contributor> result = contributorRepository
			.findByContributorNameAndContributorRole("no-one", ContributorRole.AUTHOR);

		// then
		assertThat(result).isEmpty();
	}

	// ─────────────── findContributorByNameAndRole (count query) ───────────────

	@Test
	@DisplayName("findContributorByNameAndRole - 같은 이름+역할의 수를 반환")
	void findContributorByNameAndRole_WhenExists_ReturnsCount() {
		// given: setUp()에서 base-writer / AUTHOR 하나 있음 (하지만 다른 이름)
		Contributor c1 = new Contributor(null, "dup-writer", ContributorRole.AUTHOR);
		Contributor c2 = new Contributor(null, "dup-writer", ContributorRole.AUTHOR);
		Contributor c3 = new Contributor(null, "dup-writer", ContributorRole.TRANSLATOR); // 역할 다름

		entityManager.persist(c1);
		entityManager.persist(c2);
		entityManager.persist(c3);
		entityManager.flush();
		entityManager.clear();

		// when
		Long count = contributorRepository.findContributorByNameAndRole(
			"dup-writer",
			ContributorRole.AUTHOR
		);

		// then
		assertThat(count).isEqualTo(2L); // AUTHOR 두 명만 카운트
	}

	@Test
	@DisplayName("findContributorByNameAndRole - 존재하지 않는 이름/역할이면 0 반환")
	void findContributorByNameAndRole_WhenNotExists_ReturnsZero() {
		// given
		Contributor c1 = new Contributor(null, "someone", ContributorRole.AUTHOR);
		entityManager.persist(c1);
		entityManager.flush();
		entityManager.clear();

		// when
		Long count = contributorRepository.findContributorByNameAndRole(
			"no-such-name",
			ContributorRole.AUTHOR
		);

		// then
		assertThat(count).isZero();
	}
}
