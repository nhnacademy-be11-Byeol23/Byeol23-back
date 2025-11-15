package com.nhnacademy.byeol23backend.orderset.refund.service.impl;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.*;
import static org.mockito.Mockito.*;

import java.math.BigDecimal;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import com.nhnacademy.byeol23backend.memberset.member.domain.Member;
import com.nhnacademy.byeol23backend.orderset.delivery.domain.DeliveryPolicy;
import com.nhnacademy.byeol23backend.orderset.delivery.exception.DeliveryPolicyNotFoundException;
import com.nhnacademy.byeol23backend.orderset.delivery.repository.DeliveryPolicyRepository;
import com.nhnacademy.byeol23backend.orderset.order.domain.Order;
import com.nhnacademy.byeol23backend.orderset.order.exception.OrderNotFoundException;
import com.nhnacademy.byeol23backend.orderset.order.repository.OrderRepository;
import com.nhnacademy.byeol23backend.orderset.refund.domain.Refund;
import com.nhnacademy.byeol23backend.orderset.refund.domain.dto.RefundRequest;
import com.nhnacademy.byeol23backend.orderset.refund.domain.dto.RefundResponse;
import com.nhnacademy.byeol23backend.orderset.refund.repository.RefundRepository;
import com.nhnacademy.byeol23backend.orderset.refundpolicy.domain.RefundOption;
import com.nhnacademy.byeol23backend.orderset.refundpolicy.domain.RefundPolicy;
import com.nhnacademy.byeol23backend.orderset.refundpolicy.exception.RefundPolicyNotFoundException;
import com.nhnacademy.byeol23backend.orderset.refundpolicy.repository.RefundPolicyRepository;

@ExtendWith(MockitoExtension.class)
class RefundServiceImplTest {

	@Mock
	private RefundRepository refundRepository;
	@Mock
	private RefundPolicyRepository refundPolicyRepository;
	@Mock
	private OrderRepository orderRepository;
	@Mock
	private DeliveryPolicyRepository deliveryPolicyRepository;

	@InjectMocks
	private RefundServiceImpl refundService;

	// --- Mock Objects ---
	private Order mockOrder;
	private Member mockMember;
	private RefundPolicy mockRefundPolicy;
	private DeliveryPolicy mockDeliveryPolicy;

	// --- Test DTOs ---
	private RefundRequest mindChangedRequest;
	private RefundRequest breakRequest;
	private String orderNumber = "order-123";

	@BeforeEach
	void setUp() {
		// Mock 객체들 초기화
		mockOrder = Mockito.mock(Order.class);
		mockMember = Mockito.mock(Member.class);
		mockRefundPolicy = Mockito.mock(RefundPolicy.class);
		mockDeliveryPolicy = Mockito.mock(DeliveryPolicy.class);

		// 테스트용 DTO 생성
		mindChangedRequest = new RefundRequest(orderNumber, "MIND_CHANGED", RefundOption.MIND_CHANGED);
		breakRequest = new RefundRequest(orderNumber, "BREAK", RefundOption.BREAK);
	}

	@Test
	@DisplayName("환불 요청 (단순 변심) - 회원 성공")
	void refundRequest_Success_MindChanged_Member() {
		// given
		BigDecimal actualPrice = new BigDecimal("50000");
		BigDecimal currentPoint = new BigDecimal("1000");
		BigDecimal deliveryFee = new BigDecimal("3000");
		BigDecimal finalRefundAmount = new BigDecimal("47000"); // 50000 - 3000

		given(orderRepository.findOrderByOrderNumber(orderNumber)).willReturn(Optional.of(mockOrder));
		given(mockOrder.getMember()).willReturn(mockMember);
		given(mockOrder.getActualOrderPrice()).willReturn(actualPrice);
		given(mockMember.getCurrentPoint()).willReturn(currentPoint);

		given(refundPolicyRepository.getRefundPolicyByRefundOption(RefundOption.MIND_CHANGED))
			.willReturn(Optional.of(mockRefundPolicy));
		given(deliveryPolicyRepository.findFirstByOrderByChangedAtDesc())
			.willReturn(Optional.of(mockDeliveryPolicy));
		given(mockDeliveryPolicy.getDeliveryFee()).willReturn(deliveryFee);

		// save될 Refund 객체를 캡처
		ArgumentCaptor<Refund> refundCaptor = ArgumentCaptor.forClass(Refund.class);
		given(refundRepository.save(refundCaptor.capture())).willReturn(null); // save는 void가 아니므로

		// when
		RefundResponse response = refundService.refundRequest(mindChangedRequest);

		// then
		// 1. 포인트가 올바르게 업데이트 되었는지 검증
		ArgumentCaptor<BigDecimal> pointCaptor = ArgumentCaptor.forClass(BigDecimal.class);
		verify(mockMember, times(1)).updatePoint(pointCaptor.capture());
		assertThat(pointCaptor.getValue()).isEqualByComparingTo("48000"); // 1000 + 47000

		// 2. 주문 상태가 "반품"으로 변경되었는지 검증
		verify(mockOrder, times(1)).updateOrderStatus("반품");

		// 3. Refund 객체가 올바른 값으로 저장되었는지 검증
		verify(refundRepository, times(1)).save(any(Refund.class));
		Refund savedRefund = refundCaptor.getValue();
		assertThat(savedRefund.getRefundReason()).isEqualTo("MIND_CHANGED");
		assertThat(savedRefund.getRefundPrice()).isEqualByComparingTo(finalRefundAmount);
		assertThat(savedRefund.getRefundFee()).isEqualByComparingTo(deliveryFee);
	}

	@Test
	@DisplayName("환불 요청 (파손) - 회원 성공")
	void refundRequest_Success_Break_Member() {
		// given
		BigDecimal actualPrice = new BigDecimal("50000");
		BigDecimal currentPoint = new BigDecimal("1000");
		BigDecimal finalRefundAmount = new BigDecimal("50000"); // 50000 - 0

		given(orderRepository.findOrderByOrderNumber(orderNumber)).willReturn(Optional.of(mockOrder));
		given(mockOrder.getMember()).willReturn(mockMember);
		given(mockOrder.getActualOrderPrice()).willReturn(actualPrice);
		given(mockMember.getCurrentPoint()).willReturn(currentPoint);

		given(refundPolicyRepository.getRefundPolicyByRefundOption(RefundOption.BREAK))
			.willReturn(Optional.of(mockRefundPolicy));

		ArgumentCaptor<Refund> refundCaptor = ArgumentCaptor.forClass(Refund.class);
		given(refundRepository.save(refundCaptor.capture())).willReturn(null);

		// when
		RefundResponse response = refundService.refundRequest(breakRequest);

		// then
		// 1. 포인트 업데이트 검증
		ArgumentCaptor<BigDecimal> pointCaptor = ArgumentCaptor.forClass(BigDecimal.class);
		verify(mockMember, times(1)).updatePoint(pointCaptor.capture());
		assertThat(pointCaptor.getValue()).isEqualByComparingTo("51000"); // 1000 + 50000

		// 2. 주문 상태 변경 검증
		verify(mockOrder, times(1)).updateOrderStatus("반품");

		// 3. Refund 객체 저장 검증
		Refund savedRefund = refundCaptor.getValue();
		assertThat(savedRefund.getRefundReason()).isEqualTo("BREAK");
		assertThat(savedRefund.getRefundPrice()).isEqualByComparingTo(finalRefundAmount);
		assertThat(savedRefund.getRefundFee()).isEqualByComparingTo(BigDecimal.ZERO);
	}

	@Test
	@DisplayName("환불 요청 (비회원) - NullPointerException 발생 (버그)")
	void refundRequest_NonMember_ThrowsNullPointerException() {
		// given
		// [버그 지점] 비회원 주문이라 getMember()가 null을 반환
		given(orderRepository.findOrderByOrderNumber(orderNumber)).willReturn(Optional.of(mockOrder));
		given(mockOrder.getMember()).willReturn(null);
		given(mockOrder.getActualOrderPrice()).willReturn(new BigDecimal("50000"));

		given(refundPolicyRepository.getRefundPolicyByRefundOption(RefundOption.MIND_CHANGED))
			.willReturn(Optional.of(mockRefundPolicy));
		given(deliveryPolicyRepository.findFirstByOrderByChangedAtDesc())
			.willReturn(Optional.of(mockDeliveryPolicy));
		given(mockDeliveryPolicy.getDeliveryFee()).willReturn(new BigDecimal("3000"));

		// when & then
		// 'orderedMember.updatePoint'에서 NPE가 발생해야 함
		assertThatThrownBy(() -> refundService.refundRequest(mindChangedRequest))
			.isInstanceOf(NullPointerException.class);

		// (NPE 때문에 save나 updateOrderStatus는 호출되지 않아야 함)
		verify(refundRepository, never()).save(any());
		verify(mockOrder, never()).updateOrderStatus(anyString());
	}

	@Test
	@DisplayName("환불 요청 - 주문 찾기 실패")
	void refundRequest_OrderNotFound_ThrowsException() {
		// given
		given(orderRepository.findOrderByOrderNumber(orderNumber)).willReturn(Optional.empty());

		// when & then
		assertThatThrownBy(() -> refundService.refundRequest(mindChangedRequest))
			.isInstanceOf(OrderNotFoundException.class)
			.hasMessageContaining("해당 주문 번호의 주문을 찾을 수 없습니다.: " + orderNumber);
	}

	@Test
	@DisplayName("환불 요청 - 환불 정책 찾기 실패")
	void refundRequest_RefundPolicyNotFound_ThrowsException() {
		// given
		given(orderRepository.findOrderByOrderNumber(orderNumber)).willReturn(Optional.of(mockOrder));
		given(mockOrder.getMember()).willReturn(mockMember); // NPE를 피하기 위해 Member 설정

		given(refundPolicyRepository.getRefundPolicyByRefundOption(RefundOption.MIND_CHANGED))
			.willReturn(Optional.empty());

		// when & then
		assertThatThrownBy(() -> refundService.refundRequest(mindChangedRequest))
			.isInstanceOf(RefundPolicyNotFoundException.class)
			.hasMessageContaining("해당 이름의 환불 종류를 찾을 수 없습니다.: " + RefundOption.MIND_CHANGED);
	}

	@Test
	@DisplayName("환불 요청 (단순 변심) - 배송 정책 찾기 실패")
	void refundRequest_DeliveryPolicyNotFound_ThrowsException() {
		// given
		given(orderRepository.findOrderByOrderNumber(orderNumber)).willReturn(Optional.of(mockOrder));
		given(mockOrder.getMember()).willReturn(mockMember);

		given(refundPolicyRepository.getRefundPolicyByRefundOption(RefundOption.MIND_CHANGED))
			.willReturn(Optional.of(mockRefundPolicy));

		// [핵심] 배송 정책을 찾지 못함
		given(deliveryPolicyRepository.findFirstByOrderByChangedAtDesc())
			.willReturn(Optional.empty());

		// when & then
		assertThatThrownBy(() -> refundService.refundRequest(mindChangedRequest))
			.isInstanceOf(DeliveryPolicyNotFoundException.class)
			.hasMessageContaining("현재 배송비 정책을 찾을 수 없습니다.");
	}
}