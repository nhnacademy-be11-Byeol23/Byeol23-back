package com.nhnacademy.byeol23backend.orderset.refund.service.impl;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class RefundServiceImpl implements RefundService {
	private final RefundRepository refundRepository;
	private final RefundPolicyRepository refundPolicyRepository;
	private final OrderRepository orderRepository;
	private final DeliveryPolicyRepository deliveryPolicyRepository;

	@Override
	@Transactional
	public RefundResponse refundRequest(RefundRequest request) {
		Order order = orderRepository.findOrderByOrderNumber(request.orderNumber())
			.orElseThrow(() -> new OrderNotFoundException("해당 주문 번호의 주문을 찾을 수 없습니다.: " + request.orderNumber()));

		BigDecimal actualOrderPrice = order.getActualOrderPrice();

		Refund refund = null;
		RefundPolicy refundPolicy = refundPolicyRepository.getRefundPolicyByRefundOption(request.refundOption())
			.orElseThrow(
				() -> new RefundPolicyNotFoundException("해당 이름의 환불 종류를 찾을 수 없습니다.: " + request.refundOption()));
		LocalDateTime now = LocalDateTime.now();

		if (request.refundOption().equals(RefundOption.BREAK)) {
			actualOrderPrice = getRefundFee(actualOrderPrice, new BigDecimal(0L));
			refund = Refund.of(order, refundPolicy, now, request.refundReason(), actualOrderPrice, new BigDecimal(0L));
		} else if (request.refundOption().equals(RefundOption.MIND_CHANGED)) {
			DeliveryPolicy currentDeliveryPolicy = deliveryPolicyRepository.findFirstByOrderByChangedAtDesc()
				.orElseThrow(() -> new DeliveryPolicyNotFoundException("현재 배송비 정책을 찾을 수 없습니다."));
			actualOrderPrice = getRefundFee(actualOrderPrice, currentDeliveryPolicy.getDeliveryFee());
			refund = Refund.of(order, refundPolicy, now, request.refundReason(), actualOrderPrice,
				currentDeliveryPolicy.getDeliveryFee());
		}

		// 포인트로 돌려주는 로직, 쿠폰 사용여부 확인 후 쿠폰 돌려주기
		// order.getTotalBookPrice(), order.getActualOrderPrice 차이 계산해서 포인트로 돌려주면 될 듯

		refundRepository.save(refund);
		order.updateOrderStatus("반품");

		return new RefundResponse(order.getOrderNumber(), refund.getRefundReason(), refundPolicy.getRefundOption(),
			refund.getRefundPrice(), refund.getRefundedAt());
	}

	private BigDecimal getRefundFee(BigDecimal actualOrderPrice, BigDecimal deliveryFee) {
		return actualOrderPrice.subtract(deliveryFee);
	}

}