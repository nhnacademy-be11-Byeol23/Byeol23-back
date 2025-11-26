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
public class SetDeliverySentDateJob extends QuartzJobBean {
	private final OrderRepository orderRepository;

	@Override
	protected void executeInternal(JobExecutionContext context) throws JobExecutionException {
		log.info("쿼츠 Job 실행: 결제 완료 상품 출고일 지정");

		LocalDate today = LocalDate.now();

		int updatedCount = orderRepository.updateDeliverySentDate(today);

		log.info("{}건 배송 중으로 처리", updatedCount);
	}
}
