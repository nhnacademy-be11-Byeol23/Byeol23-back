package com.nhnacademy.byeol23backend.orderset.order.controller;

import java.net.URI;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.nhnacademy.byeol23backend.memberset.member.dto.NonmemberOrderRequest;
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
import com.nhnacademy.byeol23backend.orderset.order.service.OrderService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
public class OrderController {
	private final OrderService orderService;

	@PostMapping
	public ResponseEntity<OrderPrepareResponse> prepareOrder(@Valid @RequestBody OrderPrepareRequest request,
		@CookieValue(name = "Access-Token", required = false) String accessToken) {
		OrderPrepareResponse response = orderService.prepareOrder(request, accessToken);
		return ResponseEntity.ok(response);
	}

	@PutMapping
	public ResponseEntity<OrderCreateResponse> updateOrderStatus(@RequestParam("orderNumber") String orderNumber,
		@RequestParam String orderStatus) {
		OrderCreateResponse response = orderService.updateOrderStatus(orderNumber, orderStatus);
		return ResponseEntity.ok(response);
	}

	@PostMapping("/{order-number}")
	public ResponseEntity<OrderCancelResponse> cancelOrder(@PathVariable(name = "order-number") String orderNumber,
		@Valid @RequestBody OrderCancelRequest request) {
		OrderCancelResponse response = orderService.cancelOrder(orderNumber, request);
		return ResponseEntity.ok(response);
	}

	@GetMapping("/{order-number}")
	public ResponseEntity<OrderDetailResponse> getOrderByOrderNumber(
		@PathVariable(name = "order-number") String orderNumber) {
		OrderDetailResponse response = orderService.getOrderByOrderNumber(orderNumber);

		return ResponseEntity.ok(response);
	}

	@GetMapping
	public ResponseEntity<Page<OrderInfoResponse>> searchOrders(
		@ModelAttribute OrderSearchCondition orderSearchCondition,
		Pageable pageable) {

		Page<OrderInfoResponse> responses = orderService.searchOrders(orderSearchCondition, pageable);

		return ResponseEntity.ok(responses);
	}

	@PostMapping("/points")
	public ResponseEntity<PointOrderResponse> saveOrderWithPoints(@RequestParam String orderNumber) {
		PointOrderResponse response = orderService.createOrderWithPoints(orderNumber);
		URI uri = URI.create("/api/orders/points/" + response.orderNumber());
		return ResponseEntity.created(uri).body(response);
	}

	@PostMapping("/bulk-status")
	public ResponseEntity<Void> updateBulkOrderStatus(@RequestBody OrderBulkUpdateRequest request) {
		orderService.updateBulkOrderStatus(request);
		return ResponseEntity.ok().build();
	}

	@GetMapping("/members")
	public ResponseEntity<Page<OrderDetailResponse>> getOrders(@CookieValue(name = "Access-Token") String token,
		Pageable pageable) {
		Page<OrderDetailResponse> responses = orderService.getOrders(token, pageable);
		return ResponseEntity.ok(responses);
	}

	@PostMapping("/nonmembers/lookup")
	public ResponseEntity<OrderDetailResponse> getNonMemberOrder(@Valid @RequestBody NonmemberOrderRequest request) {
		OrderDetailResponse response = orderService.getNonMemberOrder(request);
		return ResponseEntity.ok(response);
	}

}
