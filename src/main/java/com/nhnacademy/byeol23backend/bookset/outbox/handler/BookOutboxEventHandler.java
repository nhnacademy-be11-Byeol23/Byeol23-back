package com.nhnacademy.byeol23backend.bookset.outbox.handler;

import com.nhnacademy.byeol23backend.bookset.outbox.BookOutbox;

public interface BookOutboxEventHandler {
    void handle(BookOutbox bookOutbox);
    BookOutbox.EventType getEventType();
}
