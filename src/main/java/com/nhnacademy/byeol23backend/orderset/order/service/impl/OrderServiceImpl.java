package com.nhnacademy.byeol23backend.orderset.order.service.impl;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Random;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.nhnacademy.byeol23backend.bookset.book.domain.dto.BookOrderInfoResponse;
import com.nhnacademy.byeol23backend.orderset.delivery.domain.DeliveryPolicy;
import com.nhnacademy.byeol23backend.orderset.delivery.exception.DeliveryPolicyNotFoundException;
import com.nhnacademy.byeol23backend.orderset.delivery.repository.DeliveryPolicyRepository;
import com.nhnacademy.byeol23backend.orderset.order.domain.Order;
import com.nhnacademy.byeol23backend.orderset.order.domain.dto.OrderBulkUpdateRequest;
import com.nhnacademy.byeol23backend.orderset.order.domain.dto.OrderCancelRequest;
import com.nhnacademy.byeol23backend.orderset.order.domain.dto.OrderCancelResponse;
import com.nhnacademy.byeol23backend.orderset.order.domain.dto.OrderCreateResponse;
import com.nhnacademy.byeol23backend.orderset.order.domain.dto.OrderDetailResponse;
import com.nhnacademy.byeol23backend.orderset.order.domain.dto.OrderInfoResponse;
import com.nhnacademy.byeol23backend.orderset.order.domain.dto.OrderPrepareRequest;
import com.nhnacademy.byeol23backend.orderset.order.domain.dto.OrderPrepareResponse;
import com.nhnacademy.byeol23backend.orderset.order.domain.dto.OrderSearchCondition;
import com.nhnacademy.byeol23backend.orderset.order.domain.dto.PointOrderResponse;
import com.nhnacademy.byeol23backend.orderset.order.exception.OrderNotFoundException;
import com.nhnacademy.byeol23backend.orderset.order.repository.OrderRepository;
import com.nhnacademy.byeol23backend.orderset.order.service.OrderService;
import com.nhnacademy.byeol23backend.orderset.payment.domain.Payment;
import com.nhnacademy.byeol23backend.orderset.payment.domain.dto.PaymentCancelRequest;
import com.nhnacademy.byeol23backend.orderset.payment.exception.PaymentNotFoundException;
import com.nhnacademy.byeol23backend.orderset.payment.repository.PaymentRepository;
import com.nhnacademy.byeol23backend.orderset.payment.service.PaymentService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class OrderServiceImpl implements OrderService {
	private final OrderRepository orderRepository;
	private final PaymentRepository paymentRepository;
	private final PaymentService paymentService;
	private final DeliveryPolicyRepository deliveryPolicyRepository;
	private static final String ORDER_STATUS_PAYMENT_COMPLETED = "결제 완료";
	private static final String ORDER_STATUS_ORDER_CANCELED = "주문 취소";
	private static final String PAYMENT_METHOD_POINT = "포인트 결제";
	private static final String ORDER_NOT_FOUND_MESSAGE = "해당 주문 번호를 찾을 수 없습니다.: ";
	private static final String PAYMENT_NOT_FOUND_MESSAGE = "해당 결제를 찾을 수 없습니다.: ";
	private static final String DELIVERY_POLICY_NOT_FOUND_MESSAGE = "현재 배송 정책을 찾을 수 없습니다.";

	@Override
	@Transactional
	public OrderPrepareResponse prepareOrder(OrderPrepareRequest request) {
		String timeStamp = new SimpleDateFormat("yyMMddHHmmss").format(new Date());
		String randomPart = String.format("%06d", new Random().nextInt(1_000_000));
		String orderId = timeStamp + randomPart;

		DeliveryPolicy currentDeliveryPolicy = deliveryPolicyRepository.findFirstByOrderByChangedAtDesc()
			.orElseThrow(() -> new DeliveryPolicyNotFoundException(DELIVERY_POLICY_NOT_FOUND_MESSAGE));

		Order order = Order.of(orderId, request.totalBookPrice(), request.actualOrderPrice(),
			request.deliveryArrivedDate(), request.receiver(), request.postCode(),
			request.receiverAddress(), request.receiverAddressDetail(), request.receiverAddressExtra(),
			request.receiverPhone(), currentDeliveryPolicy);

		orderRepository.save(order);

		return new OrderPrepareResponse(order.getOrderNumber(), order.getActualOrderPrice(), order.getReceiver());
	}

	@Override
	@Transactional
	public OrderCreateResponse updateOrderStatus(String orderNumber, String orderStatus) {
		Order order = orderRepository.findOrderByOrderNumber(orderNumber)
			.orElseThrow(() -> new OrderNotFoundException(ORDER_NOT_FOUND_MESSAGE + orderNumber));

		order.updateOrderStatus(orderStatus);

		return new OrderCreateResponse(orderNumber, order.getTotalBookPrice(), order.getActualOrderPrice(),
			order.getOrderedAt(), order.getOrderStatus(), order.getReceiver(),
			order.getPostCode(), order.getReceiverAddress(), order.getReceiverAddressDetail(),
			order.getReceiverPhone());
	}

	@Override
	@Transactional
	public OrderCancelResponse cancelOrder(String orderNumber, OrderCancelRequest request) {
		Order order = orderRepository.findOrderByOrderNumber(orderNumber)
			.orElseThrow(() -> new OrderNotFoundException(ORDER_NOT_FOUND_MESSAGE + orderNumber));
		Payment payment = paymentRepository.findPaymentByOrder(order)
			.orElseThrow(() -> new PaymentNotFoundException(PAYMENT_NOT_FOUND_MESSAGE + order.getOrderNumber()));

		PaymentCancelRequest paymentCancelRequest = new PaymentCancelRequest(request.cancelReason(),
			payment.getPaymentKey());

		paymentService.cancelPayment(paymentCancelRequest);

		updateOrderStatusToCanceled(order.getOrderId());

		return new OrderCancelResponse(order.getOrderNumber(), order.getActualOrderPrice(), order.getOrderStatus());
	}

	@Override
	public OrderDetailResponse getOrderByOrderNumber(String orderNumber) {
		Order order = orderRepository.findOrderByOrderNumber(orderNumber)
			.orElseThrow(() -> new OrderNotFoundException(ORDER_NOT_FOUND_MESSAGE + orderNumber));

		List<BookOrderInfoResponse> bookOrderInfoResponses = List.of(
			new BookOrderInfoResponse("찍히지 않습니다 3", 1, new BigDecimal("5850")),
			new BookOrderInfoResponse("푸른 상자 20", 1, new BigDecimal("5400")),
			new BookOrderInfoResponse("별이삼샵 11", 1, new BigDecimal("14400"))
		);

		return new OrderDetailResponse(order.getOrderNumber(), order.getOrderedAt(), order.getOrderStatus(),
			order.getActualOrderPrice(),
			order.getReceiver(), order.getReceiverPhone(), order.getReceiverAddress(), order.getReceiverAddressDetail(),
			order.getPostCode(), bookOrderInfoResponses);
	}

	@Override
	public Page<OrderInfoResponse> searchOrders(OrderSearchCondition orderSearchCondition, Pageable pageable) {
		Page<OrderInfoResponse> resultPage = orderRepository.searchOrders(orderSearchCondition, pageable);

		return resultPage;
	}

	@Override
	@Transactional
	public PointOrderResponse createOrderWithPoints(String orderNumber) {
		Order order = orderRepository.findOrderByOrderNumber(orderNumber)
			.orElseThrow(() -> new OrderNotFoundException(ORDER_NOT_FOUND_MESSAGE + orderNumber));

		order.updateOrderStatus(ORDER_STATUS_PAYMENT_COMPLETED);

		return new PointOrderResponse(order.getOrderNumber(), order.getTotalBookPrice(), PAYMENT_METHOD_POINT);
	}

	@Override
	@Transactional
	public void updateBulkOrderStatus(OrderBulkUpdateRequest request) {
		List<Order> ordersToUpdate = orderRepository.findAllByOrderNumberIn(request.orderNumberLists());

		for (Order order : ordersToUpdate) {
			order.updateOrderStatus(request.status());
		}
	}

	@Transactional
	public void updateOrderStatusToCanceled(Long orderId) {
		Order order = orderRepository.findById(orderId)
			.orElseThrow(() -> new OrderNotFoundException(ORDER_NOT_FOUND_MESSAGE + orderId));

		order.updateOrderStatus(ORDER_STATUS_ORDER_CANCELED);
	}

}
