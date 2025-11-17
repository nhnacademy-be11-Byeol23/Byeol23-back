package com.nhnacademy.byeol23backend.bookset.outbox.handler.impl;

import com.nhnacademy.byeol23backend.bookset.outbox.BookOutbox;
import com.nhnacademy.byeol23backend.bookset.outbox.handler.BookOutboxEventHandler;
import lombok.RequiredArgsConstructor;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class BookOutboxUpdateEventHandler implements BookOutboxEventHandler {
    private final ElasticsearchOperations elasticsearchOperations;

    @Override
    public void handle(BookOutbox bookOutbox) {

    }

    @Override
    public BookOutbox.EventType getEventType() {
        return BookOutbox.EventType.UPDATE;
    }
}
