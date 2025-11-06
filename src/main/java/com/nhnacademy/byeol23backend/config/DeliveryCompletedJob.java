package com.nhnacademy.byeol23backend.config;

import java.time.LocalDate;

import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.scheduling.quartz.QuartzJobBean;
import org.springframework.stereotype.Component;

import com.nhnacademy.byeol23backend.orderset.order.repository.OrderRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
public class DeliveryCompletedJob extends QuartzJobBean {

	private final OrderRepository orderRepository;

	@Override
	protected void executeInternal(JobExecutionContext context) throws JobExecutionException {
		log.info("쿼츠 Job 실행: 배송 완료 상태 업데이트 시작");

		LocalDate targetDate = LocalDate.now().minusDays(2);

		int updatedCount = orderRepository.updateInDeliveryOrdersToCompleted(targetDate);

		log.info("{}건 배송 완료 처리", updatedCount);
	}
}
