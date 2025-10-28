package com.nhnacademy.byeol23backend.orderset.order.service;

import java.io.IOException;
import java.net.http.HttpResponse;
import java.util.List;

import com.nhnacademy.byeol23backend.orderset.order.domain.dto.OrderCancelRequest;
import com.nhnacademy.byeol23backend.orderset.order.domain.dto.OrderCreateResponse;
import com.nhnacademy.byeol23backend.orderset.order.domain.dto.OrderInfoResponse;
import com.nhnacademy.byeol23backend.orderset.order.domain.dto.OrderPrepareRequest;
import com.nhnacademy.byeol23backend.orderset.order.domain.dto.OrderPrepareResponse;

public interface OrderService {
	OrderPrepareResponse prepareOrder(OrderPrepareRequest request);

	OrderCreateResponse updateOrderStatus(String orderNumber);

	List<OrderInfoResponse> getAllOrders();

	HttpResponse cancelOrder(String orderNumber, OrderCancelRequest request) throws IOException, InterruptedException;
}
