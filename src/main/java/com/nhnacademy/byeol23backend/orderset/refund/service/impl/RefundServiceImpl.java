package com.nhnacademy.byeol23backend.orderset.refund.service.impl;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
import com.nhnacademy.byeol23backend.orderset.refund.service.RefundService;
import com.nhnacademy.byeol23backend.orderset.refundpolicy.domain.RefundOption;
import com.nhnacademy.byeol23backend.orderset.refundpolicy.domain.RefundPolicy;
import com.nhnacademy.byeol23backend.orderset.refundpolicy.exception.RefundPolicyNotFoundException;
import com.nhnacademy.byeol23backend.orderset.refundpolicy.repository.RefundPolicyRepository;
import com.nhnacademy.byeol23backend.pointset.pointhistories.domain.PointHistory;
import com.nhnacademy.byeol23backend.pointset.pointhistories.exception.PointHistoryNotFoundException;
import com.nhnacademy.byeol23backend.pointset.pointhistories.repository.PointHistoryRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class RefundServiceImpl implements RefundService {
	private final RefundRepository refundRepository;
	private final RefundPolicyRepository refundPolicyRepository;
	private final OrderRepository orderRepository;
	private final DeliveryPolicyRepository deliveryPolicyRepository;
	private final PointHistoryRepository pointHistoryRepository;

	@Override
	@Transactional
	public RefundResponse refundRequest(RefundRequest request) {
		// 주문을 가져옴
		Order order = orderRepository.findOrderByOrderNumber(request.orderNumber())
			.orElseThrow(() -> new OrderNotFoundException("해당 주문 번호의 주문을 찾을 수 없습니다.: " + request.orderNumber()));

		// 주문한 멤버를 가져옴
		Member orderedMember = order.getMember();
		BigDecimal actualOrderPrice = order.getActualOrderPrice();

		Refund refund = null;
		RefundPolicy refundPolicy = refundPolicyRepository.getRefundPolicyByRefundOption(request.refundOption())
			.orElseThrow(
				() -> new RefundPolicyNotFoundException("해당 이름의 환불 종류를 찾을 수 없습니다.: " + request.refundOption()));
		LocalDateTime now = LocalDateTime.now();

		PointHistory pointHistory = pointHistoryRepository.findByOrderId(order.getOrderId())
			.orElseThrow(
				() -> new PointHistoryNotFoundException("포인트 적립 내역을 찾을 수 없습니다. 주문 번호: " + order.getOrderNumber()));

		// 1. 포인트 적립 내역 가져옴
		pointHistoryRepository.findByOrderId(order.getOrderId()).ifPresent(ph -> {
			// 적립되었던 포인트를 다시 차감
			orderedMember.updatePoint(
				orderedMember.getCurrentPoint().subtract(ph.getPointAmount())
			);
		});

		// 사용자가 사용한 포인트가 있는지 확인, 포인트 적립 내역에서 적립량을 가져옴
		BigDecimal usedPoints = pointHistory.getPointAmount();

		// 사용자가 주문 시에 사용한 포인트가 있으면 사용한 포인트를 돌려줌
		if (usedPoints != null && usedPoints.compareTo(BigDecimal.ZERO) > 0) {
			orderedMember.updatePoint(orderedMember.getCurrentPoint().add(usedPoints));
		}

		// [추가] 쿠폰 사용 내역 있으면 쿠폰 다시 사용 가능 상태로 변경
		BigDecimal refundAmount = null;
		if (request.refundOption().equals(RefundOption.BREAK)) { // 파손 파지
			refundAmount = getRefundFee(actualOrderPrice, new BigDecimal(0L)); // 배송비 0원
			refund = Refund.of(order, refundPolicy, now, request.refundReason(), refundAmount, new BigDecimal(0L));
		} else if (request.refundOption().equals(RefundOption.MIND_CHANGED)) { // 단순 변심
			DeliveryPolicy currentDeliveryPolicy = deliveryPolicyRepository.findFirstByOrderByChangedAtDesc()
				.orElseThrow(() -> new DeliveryPolicyNotFoundException("현재 배송비 정책을 찾을 수 없습니다."));
			refundAmount = getRefundFee(actualOrderPrice,
				currentDeliveryPolicy.getDeliveryFee()); // 배송비는 현재 배송비 정책에 따름
			refund = Refund.of(order, refundPolicy, now, request.refundReason(), refundAmount,
				currentDeliveryPolicy.getDeliveryFee());
		} else {
			throw new RefundPolicyNotFoundException("해당 환불 정책을 찾을 수 없습니다.: " + request.refundOption());
		}

		// PaymentGate를 이용해서 결제 취소 로직을 호출해야하는지 물어볼 것

		// 나머지 금액은 포인트로 환불, 현재 포인트에 실제로 결제한 금액을 더해줌
		orderedMember.updatePoint(orderedMember.getCurrentPoint().add(refundAmount));

		refundRepository.save(refund);
		order.updateOrderStatus("반품");

		return new RefundResponse(order.getOrderNumber(), refund.getRefundReason(), refundPolicy.getRefundOption(),
			refund.getRefundPrice(), refund.getRefundedAt());
	}

	private BigDecimal getRefundFee(BigDecimal actualOrderPrice, BigDecimal deliveryFee) {
		return actualOrderPrice.subtract(deliveryFee);
	}

}