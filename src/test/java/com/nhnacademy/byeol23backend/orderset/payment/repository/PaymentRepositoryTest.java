package com.nhnacademy.byeol23backend.orderset.payment.repository;

import static org.assertj.core.api.Assertions.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.context.annotation.Import;

import com.nhnacademy.byeol23backend.config.QueryDslConfig;
import com.nhnacademy.byeol23backend.memberset.grade.domain.Grade;
import com.nhnacademy.byeol23backend.memberset.member.domain.Member;
import com.nhnacademy.byeol23backend.memberset.member.domain.RegistrationSource;
import com.nhnacademy.byeol23backend.memberset.member.domain.Role;
import com.nhnacademy.byeol23backend.orderset.delivery.domain.DeliveryPolicy;
import com.nhnacademy.byeol23backend.orderset.order.domain.Order;
import com.nhnacademy.byeol23backend.orderset.order.repository.OrderRepository;
import com.nhnacademy.byeol23backend.orderset.payment.domain.Payment;

@DataJpaTest
@Import(QueryDslConfig.class)
@Disabled
class PaymentRepositoryTest {

	@Autowired
	private PaymentRepository paymentRepository;

	@Autowired
	private OrderRepository orderRepository; // Order 저장을 위해 주입

	@Autowired
	private TestEntityManager entityManager;

	private Order testOrder;
	private Payment testPayment;
	private DeliveryPolicy testPolicy;
	private Member testMember;
	private String testPaymentKey = "paymentKey_12345";

	@BeforeEach
	void setUp() {
		// --- 의존성 엔티티 설정 (OrderRepositoryCustomImplTest와 유사) ---
		Grade testGrade = new Grade();
		testGrade.setGradeName("일반");
		testGrade.setCriterionPrice(new BigDecimal(100000));
		testGrade.setPointRate(new BigDecimal(1));
		testGrade = entityManager.merge(testGrade);

		testMember = Member.create(
			"testUser", "password", "테스트유저", "testNick",
			"01012345678", "test@email.com", LocalDate.now(),
			Role.USER, RegistrationSource.WEB, testGrade
		);
		testMember = entityManager.merge(testMember);

		testPolicy = new DeliveryPolicy();
		testPolicy.setFreeDeliveryCondition(new BigDecimal(30000));
		testPolicy.setDeliveryFee(new BigDecimal(3000));
		testPolicy.setChangedAt(LocalDateTime.now());
		testPolicy = entityManager.merge(testPolicy);

		testOrder = Order.of(
			"20251114-001", null, new BigDecimal("15000"), new BigDecimal("15000"),
			LocalDate.now().plusDays(2), "홍길동", "12345", "주소", "상세주소1", null, "01011111111",
			testMember, testPolicy, true
		);
		orderRepository.save(testOrder); // Order 저장

		// --- 테스트 대상 엔티티: Payment 생성 ---
		testPayment = new Payment(
			testPaymentKey,
			"테스트 주문",
			"간편결제",
			new BigDecimal("15000"),
			LocalDateTime.now(),
			null, // paymentApprovedAt (승인 시간은 처음엔 null일 수 있음)
			testOrder
		);
		paymentRepository.save(testPayment); // Payment 저장

		entityManager.flush();
		entityManager.clear();
	}

	@Test
	@DisplayName("Order 객체로 Payment 조회 성공")
	void findPaymentByOrder_Success() {
		// given
		// testOrder 객체는 @BeforeEach에서 생성되었지만,
		// flush/clear 이후이므로 DB에서 다시 조회해야 정확합니다.
		Order persistedOrder = orderRepository.findById(testOrder.getOrderId()).orElseThrow();

		// when
		Optional<Payment> foundPayment = paymentRepository.findPaymentByOrder(persistedOrder);

		// then
		assertThat(foundPayment).isPresent();
		assertThat(foundPayment.get().getPaymentKey()).isEqualTo(testPaymentKey);
		assertThat(foundPayment.get().getPaymentId()).isEqualTo(testPayment.getPaymentId());
	}

	@Test
	@DisplayName("존재하지 않는 Order로 Payment 조회 시 empty 반환")
	void findPaymentByOrder_NotFound() {
		// given
		// 새로운 (연관관계 없는) Order 객체 생성
		DeliveryPolicy policy = entityManager.find(DeliveryPolicy.class, testPolicy.getDeliveryPolicyId());
		Member member = entityManager.find(Member.class, testMember.getMemberId());

		Order otherOrder = Order.of(
			"999999", null, BigDecimal.TEN, BigDecimal.TEN,
			LocalDate.now(), "이방인", "00000", "주소", "상세주소", null, "01099999999",
			member, policy, true
		);
		orderRepository.save(otherOrder);
		entityManager.flush();
		entityManager.clear();

		// when
		Optional<Payment> foundPayment = paymentRepository.findPaymentByOrder(otherOrder);

		// then
		assertThat(foundPayment).isEmpty();
	}

	@Test
	@DisplayName("PaymentKey 문자열로 Payment 조회 성공")
	void findPaymentByPaymentKey_Success() {
		// when
		Optional<Payment> foundPayment = paymentRepository.findPaymentByPaymentKey(testPaymentKey);

		// then
		assertThat(foundPayment).isPresent();
		assertThat(foundPayment.get().getPaymentId()).isEqualTo(testPayment.getPaymentId());
		assertThat(foundPayment.get().getOrder().getOrderId()).isEqualTo(testOrder.getOrderId());
	}

	@Test
	@DisplayName("존재하지 않는 PaymentKey로 조회 시 empty 반환")
	void findPaymentByPaymentKey_NotFound() {
		// when
		Optional<Payment> foundPayment = paymentRepository.findPaymentByPaymentKey("non-existing-key-123");

		// then
		assertThat(foundPayment).isEmpty();
	}
}