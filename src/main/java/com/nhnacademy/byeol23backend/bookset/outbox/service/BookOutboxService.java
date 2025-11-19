package com.nhnacademy.byeol23backend.bookset.outbox.service;

import com.nhnacademy.byeol23backend.bookset.outbox.BookOutbox;
import com.nhnacademy.byeol23backend.bookset.outbox.event.BookOutboxEvent;
import com.nhnacademy.byeol23backend.bookset.outbox.repository.BookOutboxRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class BookOutboxService {
    private final ApplicationEventPublisher eventPublisher;
    private final BookOutboxRepository bookOutboxRepository;

    @Transactional
    public void publishBookOutbox(Long bookId, BookOutbox.EventType eventType) {
        BookOutbox savedOutbox = bookOutboxRepository.save(new BookOutbox(bookId, eventType));

        Long outboxId = savedOutbox.getId();
        log.info("[추가] 도서 아웃박스 이벤트 발행: {}", outboxId);
        eventPublisher.publishEvent(new BookOutboxEvent(outboxId));
    }
}
