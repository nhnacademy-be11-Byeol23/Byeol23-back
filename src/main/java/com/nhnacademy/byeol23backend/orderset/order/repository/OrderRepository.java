package com.nhnacademy.byeol23backend.orderset.order.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import com.nhnacademy.byeol23backend.orderset.order.domain.Order;

public interface OrderRepository extends JpaRepository<Order, Long>, JpaSpecificationExecutor<Order> {
	Optional<Order> findOrderByOrderNumber(String orderNumber);
}
