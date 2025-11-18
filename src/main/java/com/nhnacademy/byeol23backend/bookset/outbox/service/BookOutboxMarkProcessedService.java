package com.nhnacademy.byeol23backend.bookset.outbox.service;

import com.nhnacademy.byeol23backend.bookset.outbox.BookOutbox;
import com.nhnacademy.byeol23backend.bookset.outbox.repository.BookOutboxRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class BookOutboxMarkProcessedService {
    private final BookOutboxRepository bookOutboxRepository;

    @Transactional
    public void markProcessed(Long outboxId) {
        bookOutboxRepository.findById(outboxId).ifPresent(BookOutbox::markAsProcessed);
        log.info("아웃박스 레코드 처리 완료: {}", outboxId);
    }
}
