package com.nhnacademy.byeol23backend.bookset.outbox.exception;

import com.nhnacademy.byeol23backend.bookset.outbox.BookOutbox;

public class BookOutboxEventHandlerNotFound extends RuntimeException {
    public BookOutboxEventHandlerNotFound(BookOutbox.EventType eventType) {
        super("%s에 맞는 BookOutboxEventHandler 인스턴스를 찾을 수 없습니다.".formatted(eventType.name()));
    }
}
