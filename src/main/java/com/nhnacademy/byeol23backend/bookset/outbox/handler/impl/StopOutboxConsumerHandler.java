package com.nhnacademy.byeol23backend.bookset.outbox.handler.impl;

import com.nhnacademy.byeol23backend.bookset.outbox.consumer.ReindexStatus;
import com.nhnacademy.byeol23backend.bookset.outbox.consumer.ReindexStatusConsumer;
import com.nhnacademy.byeol23backend.bookset.outbox.handler.OutboxConsumerHandler;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.listener.MessageListenerContainer;
import org.springframework.amqp.rabbit.listener.RabbitListenerEndpointRegistry;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class StopOutboxConsumerHandler implements OutboxConsumerHandler {
    private final RabbitListenerEndpointRegistry rabbitListenerEndpointRegistry;

    @Override
    public void handle() {
        MessageListenerContainer bookOutboxListener = rabbitListenerEndpointRegistry.getListenerContainer("bookOutboxListener");
        if(bookOutboxListener.isRunning()) {
            bookOutboxListener.stop();
            log.info("배치 서버 full reindex 중 bookOutboxConsumerListener 정지");
        }
    }

    @Override
    public ReindexStatus getReindexStatus() {
        return ReindexStatus.REINDEXING;
    }
}
