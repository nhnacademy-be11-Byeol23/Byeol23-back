package com.nhnacademy.byeol23backend.orderset.order.service;

import com.nhnacademy.byeol23backend.orderset.order.domain.dto.OrderPrepareRequest;
import com.nhnacademy.byeol23backend.orderset.order.domain.dto.OrderPrepareResponse;

public interface OrderService {
	OrderPrepareResponse prepareOrder(OrderPrepareRequest request);

}
