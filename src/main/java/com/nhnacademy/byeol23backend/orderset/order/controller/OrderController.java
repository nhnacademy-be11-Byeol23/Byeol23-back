package com.nhnacademy.byeol23backend.orderset.order.controller;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpResponse;
import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.nhnacademy.byeol23backend.orderset.order.domain.dto.OrderCancelRequest;
import com.nhnacademy.byeol23backend.orderset.order.domain.dto.OrderCreateResponse;
import com.nhnacademy.byeol23backend.orderset.order.domain.dto.OrderInfoResponse;
import com.nhnacademy.byeol23backend.orderset.order.domain.dto.OrderPrepareRequest;
import com.nhnacademy.byeol23backend.orderset.order.domain.dto.OrderPrepareResponse;
import com.nhnacademy.byeol23backend.orderset.order.service.OrderService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
public class OrderController {
	private final OrderService orderService;

	@PostMapping
	public ResponseEntity<OrderPrepareResponse> createOrder(@RequestBody OrderPrepareRequest request) {
		OrderPrepareResponse response = orderService.prepareOrder(request);
		URI uri = URI.create("/api/orders/" + response.orderNumber());
		return ResponseEntity.created(uri).body(response);
	}

	@PutMapping
	public ResponseEntity<OrderCreateResponse> updateOrderStatus(@RequestParam("orderNumber") String orderNumber) {
		OrderCreateResponse response = orderService.updateOrderStatus(orderNumber);
		return ResponseEntity.ok(response);
	}

	@GetMapping
	public List<OrderInfoResponse> getAllOrders() {
		return orderService.getAllOrders();
	}

	@PostMapping("/{orderNumber}")
	public ResponseEntity<String> cancelOrder(@PathVariable String orderNumber,
		@RequestBody OrderCancelRequest request) {
		try {
			HttpResponse<String> response = orderService.cancelOrder(orderNumber, request);
			return ResponseEntity.status(response.statusCode()).body(response.body());
		} catch (IOException | InterruptedException e) {
			throw new RuntimeException(e);
		}
	}

}
