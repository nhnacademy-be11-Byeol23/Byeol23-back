package com.nhnacademy.byeol23backend.config;

import java.util.List;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.nhnacademy.byeol23backend.orderset.order.domain.Order;
import com.nhnacademy.byeol23backend.orderset.order.repository.OrderRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
public class DeliveryCompletedScheduler {
	private final OrderRepository orderRepository;

	/*
	 * 배송 중 상태가 되고 2일 후 배송 완료 상태가 되도록 스케줄링한다.
	 * 매일 자정에 스케줄링이 동작하게 된다.
	 * */
	@Scheduled(cron = "0 0 0 * * MON-FRI", zone = "Asia/Seoul")
	@Transactional
	public void updateOrderStatusToDeliveryCompleted() {
		log.info("배송 중인 상품 배송 완료로 상태 변경");
		List<Order> inDeliveryOrders = orderRepository.findAllByOrderStatus("배송중");

		for (Order order : inDeliveryOrders) {
			order.updateOrderStatus("배송 완료");
		}
	}
}
