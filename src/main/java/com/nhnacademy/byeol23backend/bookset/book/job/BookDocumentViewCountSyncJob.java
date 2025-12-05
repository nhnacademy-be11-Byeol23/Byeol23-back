package com.nhnacademy.byeol23backend.bookset.book.job;

import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.stereotype.Component;

import com.nhnacademy.byeol23backend.bookset.book.service.BookDocumentViewCountSyncService;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class BookDocumentViewCountSyncJob implements Job {
    private final BookDocumentViewCountSyncService bookDocumentViewCountSyncService;

    @Override
    public void execute(JobExecutionContext jobExecutionContext) throws JobExecutionException {
        bookDocumentViewCountSyncService.syncBookDocumentViewCount();
    }
}
