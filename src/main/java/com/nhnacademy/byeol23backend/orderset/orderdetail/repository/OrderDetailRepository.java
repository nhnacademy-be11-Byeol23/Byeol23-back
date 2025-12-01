package com.nhnacademy.byeol23backend.orderset.orderdetail.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.nhnacademy.byeol23backend.orderset.order.domain.Order;
import com.nhnacademy.byeol23backend.orderset.orderdetail.domain.OrderDetail;

public interface OrderDetailRepository extends JpaRepository<OrderDetail, Long> {

	@Query("SELECT od from OrderDetail od JOIN FETCH od.book WHERE od.order = :order")
	List<OrderDetail> findAllByOrderWithBook(@Param("order") Order order);

	@Query("SELECT od FROM OrderDetail od JOIN FETCH od.book b WHERE od.order = :order")
	List<OrderDetail> findByOrderWithBook(@Param("order") Order order);

	List<OrderDetail> findAllByBook_BookId(Long bookBookId);

	Optional<OrderDetail> findByOrder_OrderNumberAndBook_BookId(String orderOrderNumber, Long bookBookId);
}
