package com.nhnacademy.byeol23backend.bookset.book.job;

import com.nhnacademy.byeol23backend.bookset.book.service.BookViewCountSyncService;
import lombok.RequiredArgsConstructor;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class BookViewCountSyncJob implements Job {
    private final BookViewCountSyncService bookViewCountSyncService;

    // redis 에서 조회수가 증가한 도서 imageId 리스트와 조회수 값을 가져와서 해당 도서에 조회수 컬럼 값을 증분 업데이트
    @Override
    public void execute(JobExecutionContext jobExecutionContext) {
        bookViewCountSyncService.syncBookViewCount();
    }
}
