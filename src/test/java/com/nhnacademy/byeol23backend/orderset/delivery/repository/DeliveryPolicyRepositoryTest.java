package com.nhnacademy.byeol23backend.orderset.delivery.repository;

import static org.assertj.core.api.Assertions.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;

import com.nhnacademy.byeol23backend.config.QueryDslConfig;
import com.nhnacademy.byeol23backend.orderset.delivery.domain.DeliveryPolicy;

/**
 * @DataJpaTest
 * - JPA 관련 설정(Entity, Repository)만 로드하여 테스트합니다.
 * - 기본적으로 인메모리 DB(H2)를 사용합니다.
 * - 모든 테스트는 @Transactional 처리되며, 테스트 종료 시 롤백됩니다.
 */
@DataJpaTest
@Import(QueryDslConfig.class)
@TestPropertySource(properties = {"spring.sql.init.mode=never"})
class DeliveryPolicyRepositoryTest {

	@Autowired
	private DeliveryPolicyRepository deliveryPolicyRepository;

	@Test
	@DisplayName("findFirstByOrderByChangedAtDesc - 가장 최근 변경된 정책을 1건 조회한다")
	void testFindFirstByOrderByChangedAtDesc() {
		// given (Arrange)
		// [수정] 데이터를 이 테스트 메서드 내에서만 생성합니다.
		LocalDateTime now = LocalDateTime.now();
		DeliveryPolicy policyOld = new DeliveryPolicy(BigDecimal.valueOf(50000), BigDecimal.valueOf(3000),
			now.minusDays(2));
		DeliveryPolicy policyLatest = new DeliveryPolicy(BigDecimal.valueOf(60000), BigDecimal.valueOf(2500),
			now); // <-- 이게 찾아져야 함
		DeliveryPolicy policyMiddle = new DeliveryPolicy(BigDecimal.valueOf(55000), BigDecimal.valueOf(2800),
			now.minusDays(1));
		deliveryPolicyRepository.saveAll(List.of(policyOld, policyLatest, policyMiddle));

		// when (Act)
		Optional<DeliveryPolicy> resultOpt = deliveryPolicyRepository.findFirstByOrderByChangedAtDesc();

		// then (Assert)
		assertThat(resultOpt).isPresent();
		DeliveryPolicy foundPolicy = resultOpt.get();
		assertThat(foundPolicy.getDeliveryFee()).isEqualTo(policyLatest.getDeliveryFee());
	}

	@Test
	@DisplayName("findFirstByOrderByChangedAtDesc - 데이터가 없을 경우 빈 Optional을 반환한다")
	void testFindFirst_WhenEmpty() {
		// when (Act)
		Optional<DeliveryPolicy> resultOpt = deliveryPolicyRepository.findFirstByOrderByChangedAtDesc();

		// then (Assert)
		assertThat(resultOpt).isEmpty(); // 비어있는 DB에서 조회했으므로 비어있어야 함
	}
}