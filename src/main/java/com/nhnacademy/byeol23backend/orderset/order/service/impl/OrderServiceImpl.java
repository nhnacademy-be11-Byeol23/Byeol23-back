package com.nhnacademy.byeol23backend.orderset.order.service.impl;

import java.io.IOException;
import java.math.BigDecimal;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.Base64;
import java.util.Date;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.nhnacademy.byeol23backend.bookset.book.domain.dto.BookOrderInfoResponse;
import com.nhnacademy.byeol23backend.orderset.order.domain.Order;
import com.nhnacademy.byeol23backend.orderset.order.domain.dto.OrderCancelRequest;
import com.nhnacademy.byeol23backend.orderset.order.domain.dto.OrderCreateResponse;
import com.nhnacademy.byeol23backend.orderset.order.domain.dto.OrderDetailResponse;
import com.nhnacademy.byeol23backend.orderset.order.domain.dto.OrderInfoResponse;
import com.nhnacademy.byeol23backend.orderset.order.domain.dto.OrderPrepareRequest;
import com.nhnacademy.byeol23backend.orderset.order.domain.dto.OrderPrepareResponse;
import com.nhnacademy.byeol23backend.orderset.order.domain.dto.PointOrderResponse;
import com.nhnacademy.byeol23backend.orderset.order.exception.OrderNotFoundException;
import com.nhnacademy.byeol23backend.orderset.order.repository.OrderRepository;
import com.nhnacademy.byeol23backend.orderset.order.repository.OrderSpecifications;
import com.nhnacademy.byeol23backend.orderset.order.service.OrderService;
import com.nhnacademy.byeol23backend.orderset.payment.domain.Payment;
import com.nhnacademy.byeol23backend.orderset.payment.exception.PaymentNotFoundException;
import com.nhnacademy.byeol23backend.orderset.payment.repository.PaymentRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class OrderServiceImpl implements OrderService {
	private final OrderRepository orderRepository;
	private final PaymentRepository paymentRepository;
	private final ObjectMapper objectMapper;
	@Value("${tossPayment.secretKey}")
	private String secretKey;
	private static final String ORDER_STATUS_WAITING = "대기";
	private static final String ORDER_STATUS_PAYMENT_COMPLETED = "결제 완료";
	private static final String PAYMENT_METHOD_POINT = "포인트 결제";

	@Override
	@Transactional
	public OrderPrepareResponse prepareOrder(OrderPrepareRequest request) {
		String timeStamp = new SimpleDateFormat("yyMMddHHmmss").format(new Date());
		String randomPart = String.format("%06d", new Random().nextInt(1_000_000));
		String orderId = timeStamp + randomPart;

		Order order = new Order(orderId, request.totalBookPrice(), request.actualOrderPrice(),
			LocalDateTime.now(), ORDER_STATUS_WAITING, request.deliveryArrivedDate(),
			request.receiver(), request.postCode(), request.receiverAddress(),
			request.receiverAddressDetail(), request.receiverPhone());

		orderRepository.save(order);

		return new OrderPrepareResponse(order.getOrderNumber(), order.getActualOrderPrice(), order.getReceiver());
	}

	@Override
	@Transactional
	public OrderCreateResponse updateOrderStatus(String orderNumber) {
		Order order = orderRepository.findOrderByOrderNumber(orderNumber)
			.orElseThrow(() -> new OrderNotFoundException("해당 주문 번호를 찾을 수 없습니다.: " + orderNumber));

		order.setOrderStatus(ORDER_STATUS_PAYMENT_COMPLETED);

		return new OrderCreateResponse(orderNumber, order.getTotalBookPrice(), order.getActualOrderPrice(),
			order.getOrderedAt(), order.getOrderStatus(), order.getDeliveryArrivedDate(), order.getReceiver(),
			order.getPostCode(), order.getReceiverAddress(), order.getReceiverAddressDetail(),
			order.getReceiverPhone());
	}

	@Override
	@Transactional
	public HttpResponse cancelOrder(String orderNumber, OrderCancelRequest request) throws
		IOException,
		InterruptedException {
		Order order = orderRepository.findOrderByOrderNumber(orderNumber)
			.orElseThrow(() -> new OrderNotFoundException("해당 주문 번호를 찾을 수 없습니다.: " + orderNumber));
		Payment payment = paymentRepository.findPaymentByOrder(order)
			.orElseThrow(() -> new PaymentNotFoundException("해당 결제를 찾을 수 없습니다." + order.getOrderNumber()));

		JsonNode requestObj = objectMapper.createObjectNode()
			.put("cancelReason", request.cancelReason());

		String requestBody = objectMapper.writeValueAsString(requestObj);

		HttpRequest httpRequest = HttpRequest.newBuilder()
			.uri(
				URI.create("https://api.tosspayments.com/v1/payments/" + payment.getPaymentKey() + "/cancel")
			)
			.header("Authorization", getAuthorizations())
			.header("Content-Type", "application/json")
			.method("POST", HttpRequest.BodyPublishers.ofString(requestBody))
			.build();

		order.setOrderStatus("주문 취소");

		return HttpClient.newHttpClient().send(httpRequest, HttpResponse.BodyHandlers.ofString());
	}

	@Override
	public OrderDetailResponse getOrderByOrderNumber(String orderNumber) {
		Order order = orderRepository.findOrderByOrderNumber(orderNumber)
			.orElseThrow(() -> new OrderNotFoundException("해당 주문 번호를 찾을 수 없습니다.: " + orderNumber));

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
	public List<OrderInfoResponse> searchOrders(String status, String orderNumber, String receiver) {
		Specification<Order> spec = Specification.where(OrderSpecifications.statusEquals(status))
			.and(OrderSpecifications.orderNumberContains(orderNumber))
			.and(OrderSpecifications.receiverContains(receiver));

		List<Order> orderPage = orderRepository.findAll(spec);

		return orderPage.stream()
			.map(order -> new OrderInfoResponse(order.getOrderNumber(),
				order.getOrderedAt(),
				order.getReceiver(),
				order.getActualOrderPrice(),
				order.getOrderStatus()))
			.collect(Collectors.toList());
	}

	@Override
	@Transactional
	public PointOrderResponse createOrderWithPoints(String orderNumber) {
		Order order = orderRepository.findOrderByOrderNumber(orderNumber)
			.orElseThrow(() -> new OrderNotFoundException("해당 주문을 찾을 수 없습니다: " + orderNumber));

		order.setOrderStatus(ORDER_STATUS_PAYMENT_COMPLETED);

		return new PointOrderResponse(order.getOrderNumber(), order.getTotalBookPrice(), PAYMENT_METHOD_POINT);
	}

	private String getAuthorizations() {
		String originalKey = secretKey + ":";
		String encodedKey = Base64.getEncoder().encodeToString(originalKey.getBytes(StandardCharsets.UTF_8));
		return "Basic " + encodedKey;
	}

}
