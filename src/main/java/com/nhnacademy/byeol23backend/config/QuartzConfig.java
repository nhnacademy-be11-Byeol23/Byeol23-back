package com.nhnacademy.byeol23backend.config;

import com.nhnacademy.byeol23backend.bookset.book.job.BookViewCountSyncJob;
import org.quartz.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

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
                .withSchedule(CronScheduleBuilder.cronSchedule("*/30 * * * * ?").withMisfireHandlingInstructionFireAndProceed()).build();
    }
}
