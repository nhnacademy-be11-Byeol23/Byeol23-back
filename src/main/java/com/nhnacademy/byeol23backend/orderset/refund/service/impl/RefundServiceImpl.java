package com.nhnacademy.byeol23backend.orderset.refund.service.impl;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

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
import com.nhnacademy.byeol23backend.pointset.orderpoint.domain.OrderPoint;
import com.nhnacademy.byeol23backend.pointset.orderpoint.repository.OrderPointRepository;
import com.nhnacademy.byeol23backend.pointset.pointhistories.service.PointService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class RefundServiceImpl implements RefundService {
	private final RefundRepository refundRepository;
	private final RefundPolicyRepository refundPolicyRepository;
	private final OrderRepository orderRepository;
	private final DeliveryPolicyRepository deliveryPolicyRepository;
	private final OrderPointRepository orderPointRepository;
	private final PointService pointService;

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
		RefundPolicy refundPolicy = refundPolicyRepository.findByRefundOption(request.refundOption())
			.orElseThrow(
				() -> new RefundPolicyNotFoundException("해당 이름의 환불 종류를 찾을 수 없습니다.: " + request.refundOption()));
		LocalDateTime now = LocalDateTime.now();

		List<OrderPoint> orderPointList = orderPointRepository.findAllByOrder(order);

		for (OrderPoint orderPoint : orderPointList) {
			pointService.cancelPoints(orderPoint.getPointHistory());
		}

		// [추가] 쿠폰 사용 내역 있으면 쿠폰 다시 사용 가능 상태로 변경

		BigDecimal refundAmount = BigDecimal.ZERO;
		if (request.refundOption().equals(RefundOption.BREAK)) { // 파손 파본
			refundAmount = order.getActualOrderPrice(); // 배송비 0원
			refund = Refund.of(order, refundPolicy, now, request.refundReason(), refundAmount, new BigDecimal(0L));
		} else if (request.refundOption().equals(RefundOption.MIND_CHANGED)) { // 단순 변심
			DeliveryPolicy currentDeliveryPolicy = deliveryPolicyRepository.findFirstByOrderByChangedAtDesc()
				.orElseThrow(() -> new DeliveryPolicyNotFoundException("현재 배송비 정책을 찾을 수 없습니다."));
			refundAmount = order.getActualOrderPrice()
				.subtract(currentDeliveryPolicy.getDeliveryFee()); // 배송비는 현재 배송비 정책에 따름
			refund = Refund.of(order, refundPolicy, now, request.refundReason(), refundAmount,
				currentDeliveryPolicy.getDeliveryFee());
		} else {
			throw new RefundPolicyNotFoundException("해당 환불 정책을 찾을 수 없습니다.: " + request.refundOption());
		}

		// 나머지 금액은 포인트로 환불, 현재 포인트에 실제로 결제한 금액을 더해줌
		orderedMember.updatePoint(orderedMember.getCurrentPoint().add(refundAmount));

		refundRepository.save(refund);
		order.updateOrderStatus("반품");

		return new RefundResponse(order.getOrderNumber(), refund.getRefundReason(), refundPolicy.getRefundOption(),
			refund.getRefundPrice(), refund.getRefundedAt());
	}

}