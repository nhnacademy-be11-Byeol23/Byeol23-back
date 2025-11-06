package com.nhnacademy.byeol23backend.config;

import java.util.TimeZone;

import org.quartz.CronScheduleBuilder;
import org.quartz.JobBuilder;
import org.quartz.JobDetail;
import org.quartz.Trigger;
import org.quartz.TriggerBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.nhnacademy.byeol23backend.bookset.book.job.BookViewCountSyncJob;

@Configuration
public class QuartzConfig {

	@Bean
	public JobDetail bookViewCountSyncJobDetail() {
		return JobBuilder.newJob(BookViewCountSyncJob.class)
			.withIdentity("bookViewCountSyncJob")
			.withDescription("도서 조회수 동기화 작업")
			.storeDurably()
			.build();
	}

	@Bean
	public Trigger bookViewCountSyncTrigger() {
		return TriggerBuilder.newTrigger()
			.forJob(bookViewCountSyncJobDetail())
			.withIdentity("bookViewCountSyncTrigger")
			.withSchedule(
				CronScheduleBuilder.cronSchedule("*/30 * * * * ?").withMisfireHandlingInstructionFireAndProceed())
			.build();
	}

	@Bean
	public JobDetail deliveryCompletedJobDetail() {
		return JobBuilder.newJob(DeliveryCompletedJob.class)
			.withIdentity("deliveryCompletedJob")
			.withDescription("월-금 자정 배송 중 -> 배송 완료 상태 변경")
			.storeDurably()
			.build();
	}

	@Bean
	public Trigger deliveryCompletedJobTrigger() {
		CronScheduleBuilder scheduleBuilder = CronScheduleBuilder.cronSchedule("0 0 0 ? * MON-FRI")
			.inTimeZone(TimeZone.getTimeZone("Asia/Seoul"));
		return TriggerBuilder.newTrigger()
			.forJob(deliveryCompletedJobDetail())
			.withIdentity("deliveryCompletedJobTrigger")
			.withSchedule(scheduleBuilder)
			.build();
	}
}
