package com.nhnacademy.byeol23backend.bookset.outbox.handler.impl;

import com.nhnacademy.byeol23backend.bookset.outbox.BookOutbox;
import com.nhnacademy.byeol23backend.bookset.outbox.handler.BookOutboxEventHandler;
import com.nhnacademy.byeol23backend.config.BookEmbeddingRabbitProperties;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class BookOutboxAddEventHandler implements BookOutboxEventHandler {
    private final RabbitTemplate rabbitTemplate;
    private final BookEmbeddingRabbitProperties embeddingRabbitProperties;

    @Override
    public void handle(BookOutbox bookOutbox) {
        log.info("임베딩 작업 메세지 발행: {}", bookOutbox.getAggregateId());
        rabbitTemplate.convertAndSend(embeddingRabbitProperties.exchange(), embeddingRabbitProperties.routingKey(), bookOutbox.getAggregateId());
    }

    @Override
    public BookOutbox.EventType getEventType() {
        return BookOutbox.EventType.ADD;
    }
}
