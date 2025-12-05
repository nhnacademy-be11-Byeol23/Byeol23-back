package com.nhnacademy.byeol23backend.config;

import java.util.TimeZone;

import org.quartz.CronScheduleBuilder;
import org.quartz.JobBuilder;
import org.quartz.JobDetail;
import org.quartz.Trigger;
import org.quartz.TriggerBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.nhnacademy.byeol23backend.bookset.book.job.BookDocumentViewCountSyncJob;
import com.nhnacademy.byeol23backend.bookset.book.job.BookViewCountSyncJob;
import com.nhnacademy.byeol23backend.memberset.member.job.InactiveMemberJob;
import com.nhnacademy.byeol23backend.memberset.member.job.MemberGradeUpdateJob;

@Configuration
public class QuartzConfig {

	@Bean
	public JobDetail inactiveMemberJobDetail() {
		return JobBuilder.newJob(InactiveMemberJob.class)
			.withIdentity("inactiveMemberJob")
			.withDescription("마지막 로그인 날짜가 3개월 전인 회원 비활성화 작업")
			.storeDurably()
			.build();
	}

	@Bean
	public Trigger inactiveMemberJobTrigger() {
		return TriggerBuilder.newTrigger()
			.forJob(inactiveMemberJobDetail())
			.withIdentity("inactiveMemberJobTrigger")
			.withSchedule(
				CronScheduleBuilder.cronSchedule("0 0 1 * * ?")
			)
			.build();
	}

	@Bean
	public JobDetail updateMemberGradeJobDetail() {
		return JobBuilder.newJob(MemberGradeUpdateJob.class)
			.withIdentity("updateMemberGradeJob")
			.withDescription("회원 등급 자동 업데이트 작업")
			.storeDurably()
			.build();
	}

	@Bean
	Trigger updateMemberGradeTrigger() {
		return TriggerBuilder.newTrigger()
			.forJob(updateMemberGradeJobDetail())
			.withIdentity("updateMemberGradeJobTrigger")
			.withSchedule(CronScheduleBuilder.cronSchedule("0 0 1 * * ?"))
			.build();
	}

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
				CronScheduleBuilder.cronSchedule("0 0 * * * ?").withMisfireHandlingInstructionFireAndProceed()
			)
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

	@Bean
	public JobDetail setDeliverySentDateJobDetail() {
		return JobBuilder.newJob(SetDeliverySentDateJob.class)
			.withIdentity("setDeliverySentDateJob")
			.withDescription("월-금 자정 결제 완료이고, 출고일이 없는 배송 -> 출고일 지정")
			.storeDurably()
			.build();
	}

	@Bean
	public Trigger setDeliverySentDateJobTrigger() {
		CronScheduleBuilder scheduleBuilder = CronScheduleBuilder.cronSchedule("0 0 0 ? * MON-FRI")
			.inTimeZone(TimeZone.getTimeZone("Asia/Seoul"));
		return TriggerBuilder.newTrigger()
			.forJob(setDeliverySentDateJobDetail())
			.withIdentity("setDeliverySentDateJob")
			.withSchedule(scheduleBuilder)
			.build();
	}

	@Bean
	public JobDetail birthdayCouponJobDetail() {
		return JobBuilder.newJob(BirthdayCouponJob.class)
			.withIdentity("birthdayCouponJob")
			.withDescription("매월 1일 생일자 대상 쿠폰 발급 요청 Job")
			.storeDurably()
			.build();
	}

	@Bean
	public Trigger birthdayCouponJobTrigger() {
		// Cron 표현식: 0초 10분 1시 1일 *월 ?요일
		CronScheduleBuilder scheduleBuilder = CronScheduleBuilder.cronSchedule("0 52 17 * * ?")
			.inTimeZone(TimeZone.getTimeZone("Asia/Seoul"));

		return TriggerBuilder.newTrigger()
			.forJob(birthdayCouponJobDetail())
			.withIdentity("birthdayCouponJobTrigger")
			.withSchedule(scheduleBuilder)
			.build();
	}
}
