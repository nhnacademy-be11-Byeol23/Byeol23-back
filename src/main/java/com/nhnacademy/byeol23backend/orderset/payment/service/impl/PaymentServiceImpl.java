package com.nhnacademy.byeol23backend.orderset.payment.service.impl;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.nhnacademy.byeol23backend.orderset.order.domain.Order;
import com.nhnacademy.byeol23backend.orderset.order.exception.OrderNotFoundException;
import com.nhnacademy.byeol23backend.orderset.order.repository.OrderRepository;
import com.nhnacademy.byeol23backend.orderset.payment.domain.Payment;
import com.nhnacademy.byeol23backend.orderset.payment.domain.PaymentProvider;
import com.nhnacademy.byeol23backend.orderset.payment.domain.dto.PaymentCancelRequest;
import com.nhnacademy.byeol23backend.orderset.payment.domain.dto.PaymentCancelResponse;
import com.nhnacademy.byeol23backend.orderset.payment.domain.dto.PaymentConfirmResponse;
import com.nhnacademy.byeol23backend.orderset.payment.domain.dto.PaymentParamRequest;
import com.nhnacademy.byeol23backend.orderset.payment.domain.dto.PaymentResultResponse;
import com.nhnacademy.byeol23backend.orderset.payment.exception.PaymentNotFoundException;
import com.nhnacademy.byeol23backend.orderset.payment.repository.PaymentRepository;
import com.nhnacademy.byeol23backend.orderset.payment.service.PaymentService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PaymentServiceImpl implements PaymentService {
	private final PaymentFacade paymentFacade;
	private final PaymentRepository paymentRepository;
	private final OrderRepository orderRepository;
	private static final String ORDER_NOT_FOUND_MESSAGE = "해당 주문을 찾을 수 없습니다.: ";

	@Override
	@Transactional
	public PaymentResultResponse confirmPayment(PaymentParamRequest paymentParamRequest) {
		Order order = orderRepository.findOrderByOrderNumber(paymentParamRequest.orderId())
			.orElseThrow(() -> new OrderNotFoundException(ORDER_NOT_FOUND_MESSAGE + paymentParamRequest.orderId()));

		PaymentConfirmResponse confirmResponse = paymentFacade.confirmPayment(PaymentProvider.TOSS_PAYMENTS,
			paymentParamRequest);

		order.updateOrderStatus("결제 완료");

		return new PaymentResultResponse(confirmResponse.paymentKey(), confirmResponse.orderId(),
			confirmResponse.orderName(),
			confirmResponse.status(), confirmResponse.totalAmount(), confirmResponse.paymentRequestAt(),
			confirmResponse.paymentApprovedAt(), confirmResponse.method());
	}

	@Override
	@Transactional
	public PaymentCancelResponse cancelPayment(PaymentCancelRequest paymentCancelRequest) {
		Payment payment = paymentRepository.findPaymentByPaymentKey(paymentCancelRequest.paymentKey())
			.orElseThrow(
				() -> new PaymentNotFoundException("해당 결제키를 찾을 수 없습니다.: " + paymentCancelRequest.paymentKey()));

		Order order = payment.getOrder();
		if (order == null) {
			throw new IllegalArgumentException("결제에 연결된 주문이 없습니다.");
		}

		PaymentCancelResponse cancelResponse = paymentFacade.cancelPayment(PaymentProvider.TOSS_PAYMENTS,
			paymentCancelRequest);

		order.updateOrderStatus("주문 취소");

		return cancelResponse;
	}

	@Override
	@Transactional
	public void createPayment(PaymentResultResponse response) {

		Order order = orderRepository.findOrderByOrderNumber(response.orderId())
			.orElseThrow(() -> new OrderNotFoundException(ORDER_NOT_FOUND_MESSAGE + response.orderId()));

		Payment payment = new Payment(response.paymentKey(), response.orderName(), response.method(),
			response.totalAmount(), response.paymentApprovedAt(), response.paymentApprovedAt(), order);

		paymentRepository.save(payment);

	}

}
