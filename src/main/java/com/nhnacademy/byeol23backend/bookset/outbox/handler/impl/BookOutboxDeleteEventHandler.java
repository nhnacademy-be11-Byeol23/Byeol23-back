package com.nhnacademy.byeol23backend.bookset.outbox.handler.impl;

import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.stereotype.Component;

import com.nhnacademy.byeol23backend.bookset.book.document.BookDocument;
import com.nhnacademy.byeol23backend.bookset.outbox.BookOutbox;
import com.nhnacademy.byeol23backend.bookset.outbox.handler.BookOutboxEventHandler;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
public class BookOutboxDeleteEventHandler implements BookOutboxEventHandler {
    private final ElasticsearchOperations elasticsearchOperations;

    @Override
    public void handle(BookOutbox bookOutbox) {
        Long bookId = bookOutbox.getAggregateId();
        elasticsearchOperations.delete(String.valueOf(bookId), BookDocument.class);
        log.info("엘라스틱서치에 도서 문서 삭제: {}", bookId);
    }

    @Override
    public BookOutbox.EventType getEventType() {
        return BookOutbox.EventType.DELETE;
    }
}
