package com.nhnacademy.byeol23backend.bookset.outbox.consumer;

import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import com.nhnacademy.byeol23backend.bookset.outbox.handler.OutboxConsumerHandler;
import com.nhnacademy.byeol23backend.bookset.outbox.handler.OutboxConsumerHandlerRegistry;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
public class ReindexStatusConsumer {
    private final OutboxConsumerHandlerRegistry outboxConsumerHandlerRegistry;

    @RabbitListener(queues = "${book.reindex.queue}")
    public void consume(String message) {
        OutboxConsumerHandler outboxConsumerHandler = outboxConsumerHandlerRegistry.getOutboxConsumerHandler(ReindexStatus.valueOf(message));
        outboxConsumerHandler.handle();
    }
}
