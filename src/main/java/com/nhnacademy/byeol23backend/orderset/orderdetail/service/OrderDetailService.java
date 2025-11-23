package com.nhnacademy.byeol23backend.orderset.orderdetail.service;

import java.util.List;

import com.nhnacademy.byeol23backend.orderset.orderdetail.domain.OrderDetail;

public interface OrderDetailService {
	List<OrderDetail> getOrderDetailsByBookId(Long bookId);
	OrderDetail getOrderDetailById(Long orderDetailId);
	List<OrderDetail> getReviewableOrderDetailsByMemberId(Long memberId);
}
