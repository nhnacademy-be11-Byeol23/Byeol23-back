package com.nhnacademy.byeol23backend.pointset.orderpoint.repository;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.nhnacademy.byeol23backend.orderset.order.domain.Order;
import com.nhnacademy.byeol23backend.pointset.orderpoint.domain.OrderPoint;

public interface OrderPointRepository extends JpaRepository<OrderPoint, Long> {
	List<OrderPoint> findAllByOrder(Order order);

	@Query("""
				SELECT ph.pointAmount FROM OrderPoint op
				INNER JOIN op.pointHistory ph
				INNER JOIN op.order o
				WHERE o = :order AND ph.pointAmount < 0
		""")
	BigDecimal findUsedPointsAmountByOrder(@Param("order") Order order);

}
