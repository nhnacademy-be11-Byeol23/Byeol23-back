package com.nhnacademy.byeol23backend.bookset.outbox.handler;

import com.nhnacademy.byeol23backend.bookset.outbox.BookOutbox;
import com.nhnacademy.byeol23backend.bookset.outbox.exception.BookOutboxEventHandlerNotFound;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class BookOutboxEventHandlerRegistry {
    private final List<BookOutboxEventHandler> handlers;

    public BookOutboxEventHandler getBookOutboxEventHandler(BookOutbox.EventType eventType) {
        return handlers.stream()
                .filter(handler -> handler.getEventType() == eventType)
                .findFirst()
                .orElseThrow(() -> new BookOutboxEventHandlerNotFound(eventType));
    }
}
