package com.nhnacademy.byeol23backend.orderset.order.repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import com.nhnacademy.byeol23backend.orderset.order.domain.Order;

public interface OrderRepository extends JpaRepository<Order, Long>, OrderRepositoryCustom {
	Optional<Order> findOrderByOrderNumber(String orderNumber);

	@Modifying
	@Transactional
	@Query("UPDATE Order o SET o.orderStatus = :orderStatus WHERE o.orderNumber In :orderNumbers")
	int updateOrderStatusByOrderNumbers(@Param("orderNumbers") List<String> orderNumbers,
		@Param("orderStatus") String orderStatus);

	@Modifying
	@Transactional
	@Query("UPDATE Order o SET o.orderStatus = '배송 완료' " +
		"WHERE o.orderStatus = '배송중' " +
		"AND o.deliverySentDate <= :targetDate")
	int updateInDeliveryOrdersToCompleted(@Param("targetDate") LocalDate targetDate);
}
