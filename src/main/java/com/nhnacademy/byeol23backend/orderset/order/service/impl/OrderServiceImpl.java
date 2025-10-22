package com.nhnacademy.byeol23backend.orderset.order.service.impl;

import java.time.LocalDateTime;

import org.springframework.stereotype.Service;

import com.nhnacademy.byeol23backend.orderset.order.domain.Order;
import com.nhnacademy.byeol23backend.orderset.order.domain.dto.OrderPrepareRequest;
import com.nhnacademy.byeol23backend.orderset.order.domain.dto.OrderPrepareResponse;
import com.nhnacademy.byeol23backend.orderset.order.repository.OrderRepository;
import com.nhnacademy.byeol23backend.orderset.order.service.OrderService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {
	private final OrderRepository orderRepository;

	@Override
	public OrderPrepareResponse prepareOrder(OrderPrepareRequest request) {

		Order order = new Order(request.orderNumber(), request.totalBookPrice(), request.actualOrderPrice(),
			LocalDateTime.now(), "대기", LocalDateTime.now().plusDays(3).toLocalDate(), request.receiver(),
			request.postCode(), request.receiverAddress(), request.receiverAddressDetail(), request.receiverPhone());

		orderRepository.save(order);

		return new OrderPrepareResponse(order.getOrderNumber());
	}

}
