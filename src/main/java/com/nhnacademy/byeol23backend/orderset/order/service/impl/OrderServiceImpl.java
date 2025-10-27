package com.nhnacademy.byeol23backend.orderset.order.service.impl;

import java.io.IOException;
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
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.nhnacademy.byeol23backend.orderset.order.domain.Order;
import com.nhnacademy.byeol23backend.orderset.order.domain.dto.OrderCancelRequest;
import com.nhnacademy.byeol23backend.orderset.order.domain.dto.OrderCreateResponse;
import com.nhnacademy.byeol23backend.orderset.order.domain.dto.OrderInfoResponse;
import com.nhnacademy.byeol23backend.orderset.order.domain.dto.OrderPrepareRequest;
import com.nhnacademy.byeol23backend.orderset.order.domain.dto.OrderPrepareResponse;
import com.nhnacademy.byeol23backend.orderset.order.exception.OrderNotFoundException;
import com.nhnacademy.byeol23backend.orderset.order.repository.OrderRepository;
import com.nhnacademy.byeol23backend.orderset.order.service.OrderService;
import com.nhnacademy.byeol23backend.orderset.payment.domain.Payment;
import com.nhnacademy.byeol23backend.orderset.payment.exception.PaymentNotFoundException;
import com.nhnacademy.byeol23backend.orderset.payment.repository.PaymentRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {
	private final OrderRepository orderRepository;
	private final PaymentRepository paymentRepository;
	private final ObjectMapper objectMapper;
	@Value("${tossPayment.secretKey}")
	private String secretKey;

	@Override
	@Transactional
	public OrderPrepareResponse prepareOrder(OrderPrepareRequest request) {
		String timeStamp = new SimpleDateFormat("yyMMddHHmmss").format(new Date());
		String randomPart = String.format("%06d", new Random().nextInt(1_000_000));
		String orderId = timeStamp + randomPart;

		Order order = new Order(orderId, request.totalBookPrice(), request.actualOrderPrice(),
			LocalDateTime.now(), "대기", LocalDateTime.now().plusDays(3).toLocalDate(), request.receiver(),
			request.postCode(), request.receiverAddress(), request.receiverAddressDetail(), request.receiverPhone());

		orderRepository.save(order);

		return new OrderPrepareResponse(order.getOrderNumber(), order.getActualOrderPrice(), order.getReceiver());
	}

	@Override
	@Transactional
	public OrderCreateResponse updateOrderStatus(String orderNumber) {
		Order order = orderRepository.findOrderByOrderNumber(orderNumber)
			.orElseThrow(() -> new OrderNotFoundException("해당 주문 번호를 찾을 수 없습니다.: " + orderNumber));

		order.setOrderStatus("결제 완료");

		return new OrderCreateResponse(orderNumber, order.getTotalBookPrice(), order.getActualOrderPrice(),
			order.getOrderedAt(), order.getOrderStatus(), order.getDeliveryArrivedDate(), order.getReceiver(),
			order.getPostCode(), order.getReceiverAddress(), order.getReceiverAddressDetail(),
			order.getReceiverPhone());
	}

	@Override
	@Transactional(readOnly = true)
	public List<OrderInfoResponse> getAllOrders() {
		List<Order> orderList = orderRepository.findAll();

		return orderList.stream()
			.map(order -> new OrderInfoResponse(
				order.getOrderNumber(),
				order.getOrderedAt(),
				order.getReceiver(),
				order.getActualOrderPrice(),
				order.getOrderStatus()
			))
			.collect(Collectors.toList());
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

	private String getAuthorizations() {
		String originalKey = secretKey + ":";
		String encodedKey = Base64.getEncoder().encodeToString(originalKey.getBytes(StandardCharsets.UTF_8));
		return "Basic " + encodedKey;
	}

}
