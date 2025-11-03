package com.nhnacademy.byeol23backend.orderset.order.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.nhnacademy.byeol23backend.orderset.order.domain.dto.OrderCancelRequest;
import com.nhnacademy.byeol23backend.orderset.order.domain.dto.OrderCancelResponse;
import com.nhnacademy.byeol23backend.orderset.order.domain.dto.OrderCreateResponse;
import com.nhnacademy.byeol23backend.orderset.order.domain.dto.OrderDetailResponse;
import com.nhnacademy.byeol23backend.orderset.order.domain.dto.OrderInfoResponse;
import com.nhnacademy.byeol23backend.orderset.order.domain.dto.OrderPrepareRequest;
import com.nhnacademy.byeol23backend.orderset.order.domain.dto.OrderPrepareResponse;
import com.nhnacademy.byeol23backend.orderset.order.domain.dto.OrderSearchCondition;
import com.nhnacademy.byeol23backend.orderset.order.domain.dto.PointOrderResponse;

public interface OrderService {
	OrderPrepareResponse prepareOrder(OrderPrepareRequest request);

	OrderCreateResponse updateOrderStatus(String orderNumber);

	OrderCancelResponse cancelOrder(String orderNumber, OrderCancelRequest request);

	OrderDetailResponse getOrderByOrderNumber(String orderNumber);

	Page<OrderInfoResponse> searchOrders(OrderSearchCondition orderSearchCondition, Pageable pageable);

	PointOrderResponse createOrderWithPoints(String orderNumber);
}
