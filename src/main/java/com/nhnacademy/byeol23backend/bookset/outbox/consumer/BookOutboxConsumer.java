package com.nhnacademy.byeol23backend.bookset.outbox.consumer;

import com.nhnacademy.byeol23backend.bookset.outbox.BookOutbox;
import com.nhnacademy.byeol23backend.bookset.outbox.exception.BookOutboxNotFoundException;
import com.nhnacademy.byeol23backend.bookset.outbox.handler.BookOutboxEventHandler;
import com.nhnacademy.byeol23backend.bookset.outbox.handler.BookOutboxEventHandlerRegistry;
import com.nhnacademy.byeol23backend.bookset.outbox.repository.BookOutboxRepository;
import com.nhnacademy.byeol23backend.bookset.outbox.service.BookOutboxMarkProcessedService;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class BookOutboxConsumer {
    private final BookOutboxRepository bookOutboxRepository;
    private final BookOutboxEventHandlerRegistry bookOutboxEventHandlerRegistry;
    private final BookOutboxMarkProcessedService bookOutboxMarkProcessedService;

    @RabbitListener(id = "bookOutboxListener", queues = "${book.outbox.queue}", autoStartup = "true")
    public void consume(Long outboxId) {
        BookOutbox bookOutbox = bookOutboxRepository.findById(outboxId).orElseThrow(() -> new BookOutboxNotFoundException("아웃박스 레코드를 찾을 수 없습니다."));
        BookOutboxEventHandler bookOutboxEventHandler = bookOutboxEventHandlerRegistry.getBookOutboxEventHandler(bookOutbox.getEventType());
        bookOutboxEventHandler.handle(bookOutbox);

        bookOutboxMarkProcessedService.markProcessed(outboxId);
    }
}
