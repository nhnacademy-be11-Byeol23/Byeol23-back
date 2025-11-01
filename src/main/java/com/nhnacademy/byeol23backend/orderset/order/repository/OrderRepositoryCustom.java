package com.nhnacademy.byeol23backend.orderset.order.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.nhnacademy.byeol23backend.orderset.order.domain.dto.OrderInfoResponse;
import com.nhnacademy.byeol23backend.orderset.order.domain.dto.OrderSearchCondition;

public interface OrderRepositoryCustom {
	Page<OrderInfoResponse> searchOrders(OrderSearchCondition orderSearchCondition, Pageable pageable);
}
