package com.nhnacademy.byeol23backend.config;

import com.nhnacademy.byeol23backend.bookset.book.job.BookDocumentViewCountSyncJob;
import com.nhnacademy.byeol23backend.bookset.book.job.BookViewCountSyncJob;
import org.quartz.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.TimeZone;

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
				CronScheduleBuilder.cronSchedule("0 0 * * * ?").withMisfireHandlingInstructionFireAndProceed())
			.build();
	}

    @Bean
    public JobDetail bookDocumentViewCountSyncJobDetail() {
        return JobBuilder.newJob(BookDocumentViewCountSyncJob.class)
                .withIdentity("bookDocumentViewCountSyncJob")
                .withDescription("도서 문서 조회수 동기화 작업")
                .storeDurably()
                .build();
    }

    @Bean
    public Trigger bookDocumentViewCountSyncTrigger() {
        return TriggerBuilder.newTrigger()
                .forJob(bookDocumentViewCountSyncJobDetail())
                .withIdentity("bookDocumentViewCountSyncTrigger")
                .withSchedule(
                        CronScheduleBuilder.weeklyOnDayAndHourAndMinute(1, 0, 0).withMisfireHandlingInstructionFireAndProceed()
                )
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
