package com.nhnacademy.byeol23backend.orderset.order.repository.impl;

import static org.assertj.core.api.Assertions.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import com.nhnacademy.byeol23backend.config.QueryDslConfig;
import com.nhnacademy.byeol23backend.memberset.grade.domain.Grade;
import com.nhnacademy.byeol23backend.memberset.member.domain.Member;
import com.nhnacademy.byeol23backend.memberset.member.domain.RegistrationSource;
import com.nhnacademy.byeol23backend.memberset.member.domain.Role;
import com.nhnacademy.byeol23backend.orderset.delivery.domain.DeliveryPolicy;
import com.nhnacademy.byeol23backend.orderset.order.domain.Order;
import com.nhnacademy.byeol23backend.orderset.order.domain.dto.OrderInfoResponse;
import com.nhnacademy.byeol23backend.orderset.order.domain.dto.OrderSearchCondition;
import com.nhnacademy.byeol23backend.orderset.order.repository.OrderRepository;

@DataJpaTest
@Import(QueryDslConfig.class)
@Disabled
class OrderRepositoryCustomImplTest {

	@Autowired
	private OrderRepository orderRepository;

	@Autowired
	private TestEntityManager entityManager;

	private Member testMember;
	private DeliveryPolicy testPolicy;
	private Order order1;
	private Order order2;
	private Order order3;

	@BeforeEach
	void setUp() {
		// 1. 의존성 엔티티: Grade 생성 (ID 없이)
		Grade testGrade = new Grade();
		// (Grade 엔티티에 맞게 setter가 필요합니다)
		testGrade.setGradeName("일반");
		testGrade.setCriterionPrice(new BigDecimal(100000));
		testGrade.setPointRate(new BigDecimal(1));

		// ▼▼▼ [수정] persistAndFlush -> merge ▼▼▼
		// 'detached entity' 오류를 피하기 위해 merge를 사용합니다.
		testGrade = entityManager.merge(testGrade);
		// ▲▲▲ [수정 완료] ▲▲▲

		// 2. 의존성 엔티티: Member 생성
		testMember = Member.create(
			"testUser", "password", "테스트유저", "testNick",
			"01012345678", "test@email.com", LocalDate.now(),
			Role.USER, RegistrationSource.WEB, testGrade // 영속화된 testGrade 사용
		);
		testMember = entityManager.merge(testMember); // merge로 통일

		// 3. 의존성 엔티티: DeliveryPolicy 생성 (ID 없이)
		testPolicy = new DeliveryPolicy();
		// (DeliveryPolicy 엔티티에 맞게 setter가 필요합니다)
		testPolicy.setFreeDeliveryCondition(new BigDecimal(30000));
		testPolicy.setDeliveryFee(new BigDecimal(3000));
		testPolicy.setChangedAt(LocalDateTime.now());

		testPolicy = entityManager.merge(testPolicy); // merge로 통일

		// 4. 테스트 데이터 생성: Order.of() 팩토리 메서드 사용
		order1 = Order.of(
			"20251114-001", null, new BigDecimal("15000"), new BigDecimal("15000"),
			LocalDate.now().plusDays(2), "홍길동", "12345", "주소", "상세주소1", null, "01011111111",
			testMember, testPolicy, true
		);
		order1.updateOrderStatus("ORDERED");

		order2 = Order.of(
			"20251114-002", null, new BigDecimal("25000"), new BigDecimal("25000"),
			LocalDate.now().plusDays(2), "이몽룡", "23456", "주소2", "상세주소2", null, "01022222222",
			testMember, testPolicy, true
		);
		order2.updateOrderStatus("SHIPPED");

		order3 = Order.of(
			"20251113-001", null, new BigDecimal("30000"), new BigDecimal("30000"),
			LocalDate.now().plusDays(2), "홍길동", "34567", "주소3", "상세주소3", null, "01033333333",
			testMember, testPolicy, true
		);
		order3.updateOrderStatus("ORDERED");

		// 5. DB에 저장
		orderRepository.saveAll(List.of(order1, order2, order3));

		// 6. [수정] merge를 사용했으므로 flush/clear를 호출하여 영속성 컨텍스트를 정리
		entityManager.flush();
		entityManager.clear();
	}

	@Test
	@DisplayName("조건 없이 전체 조회 (페이징 적용)")
	void searchOrders_NoCondition_WithPaging() {
		// given
		OrderSearchCondition condition = new OrderSearchCondition(null, null, null);
		Pageable pageable = PageRequest.of(0, 2); // 첫 번째 페이지, 2개씩

		// when
		Page<OrderInfoResponse> result = orderRepository.searchOrders(condition, pageable);

		// then
		assertThat(result.getTotalElements()).isEqualTo(3); // 전체 개수는 3개
		assertThat(result.getTotalPages()).isEqualTo(2);    // 2페이지
		assertThat(result.getContent()).hasSize(2);         // 현재 페이지 컨텐츠는 2개
	}

	@Test
	@DisplayName("주문 상태(status)로 필터링")
	void searchOrders_ByStatus() {
		// given
		OrderSearchCondition condition = new OrderSearchCondition("ORDERED", null, null);
		Pageable pageable = PageRequest.of(0, 10);

		// when
		Page<OrderInfoResponse> result = orderRepository.searchOrders(condition, pageable);

		// then
		assertThat(result.getTotalElements()).isEqualTo(2);
		assertThat(result.getContent()).hasSize(2);
		assertThat(result.getContent())
			.extracting(OrderInfoResponse::orderStatus) // (record 필드 접근)
			.containsOnly("ORDERED");
	}

	@Test
	@DisplayName("주문 번호(orderNumber)로 필터링 (contains)")
	void searchOrders_ByOrderNumberContains() {
		// given
		OrderSearchCondition condition = new OrderSearchCondition(null, "20251114", null); // "20251114" 포함
		Pageable pageable = PageRequest.of(0, 10);

		// when
		Page<OrderInfoResponse> result = orderRepository.searchOrders(condition, pageable);

		// then
		assertThat(result.getTotalElements()).isEqualTo(2);
		assertThat(result.getContent().get(0).orderNumber()).isEqualTo("20251114-001");
		assertThat(result.getContent().get(1).orderNumber()).isEqualTo("20251114-002");
	}

	@Test
	@DisplayName("수령인(receiver)으로 필터링")
	void searchOrders_ByReceiver() {

		OrderSearchCondition condition = new OrderSearchCondition(null, null, "홍길동");
		Pageable pageable = PageRequest.of(0, 10);

		// when
		Page<OrderInfoResponse> result = orderRepository.searchOrders(condition, pageable);

		// then
		assertThat(result.getTotalElements()).isEqualTo(2);
		assertThat(result.getContent()).hasSize(2);
		assertThat(result.getContent())
			.extracting(OrderInfoResponse::receiver) // (record 필드 접근)
			.containsOnly("홍길동");
	}

	@Test
	@DisplayName("모든 조건 결합하여 필터링")
	void searchOrders_ByAllConditions() {
		// given
		// OrderSearchCondition DTO에 receiver가 없으므로 "001"과 "ORDERED"로만 검색합니다.
		OrderSearchCondition condition = new OrderSearchCondition("ORDERED", "20251114-001", "홍길동");
		Pageable pageable = PageRequest.of(0, 10);

		// when
		Page<OrderInfoResponse> result = orderRepository.searchOrders(condition, pageable);

		// then
		assertThat(result.getTotalElements()).isEqualTo(1); // "20251114-001" (order1)
		assertThat(result.getContent()).hasSize(1);
		assertThat(result.getContent().get(0).orderNumber()).isEqualTo("20251114-001");
	}

	@Test
	@DisplayName("결과가 없는 경우")
	void searchOrders_NoResults() {
		// given
		OrderSearchCondition condition = new OrderSearchCondition("CANCELLED", null, null);
		Pageable pageable = PageRequest.of(0, 10);

		// when
		Page<OrderInfoResponse> result = orderRepository.searchOrders(condition, pageable);

		// then
		assertThat(result.getTotalElements()).isEqualTo(0);
		assertThat(result.getContent()).isEmpty();
	}
}