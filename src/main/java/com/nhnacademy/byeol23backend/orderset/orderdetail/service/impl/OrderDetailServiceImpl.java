package com.nhnacademy.byeol23backend.orderset.orderdetail.service.impl;

import java.util.List;

import com.nhnacademy.byeol23backend.orderset.orderdetail.domain.OrderDetail;
import com.nhnacademy.byeol23backend.orderset.orderdetail.repository.OrderDetailRepository;
import com.nhnacademy.byeol23backend.orderset.orderdetail.service.OrderDetailService;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class OrderDetailServiceImpl implements OrderDetailService {
	private final OrderDetailRepository orderDetailRepository;
	@Override
	public List<OrderDetail> getOrderDetailsByBookId(Long bookId) {
		return orderDetailRepository.findAllByBook_BookId(bookId);
	}

	@Override
	public OrderDetail getOrderDetailById(Long orderDetailId) {
		return orderDetailRepository.findById(orderDetailId)
			.orElseThrow(() -> new IllegalArgumentException("No order detail found for id: " + orderDetailId));
	}

	@Override
	public List<OrderDetail> getReviewableOrderDetailsByMemberId(Long memberId) {
		return List.of();
	}

	@Override
	public OrderDetail getOrderDetailByOrderNumberAndBookId(String orderNumber, Long bookId) {
		return orderDetailRepository.findByOrder_OrderNumberAndBook_BookId(orderNumber, bookId)
			.orElseThrow(() -> new IllegalArgumentException("No order detail found for order number: " + orderNumber + " and book id: " + bookId));
	}

}
