package com.nhnacademy.byeol23backend.bookset.outbox.handler.impl;

import org.springframework.amqp.rabbit.listener.MessageListenerContainer;
import org.springframework.amqp.rabbit.listener.RabbitListenerEndpointRegistry;
import org.springframework.stereotype.Component;

import com.nhnacademy.byeol23backend.bookset.outbox.consumer.ReindexStatus;
import com.nhnacademy.byeol23backend.bookset.outbox.handler.OutboxConsumerHandler;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
public class StartOutboxConsumerHandler implements OutboxConsumerHandler {
    private final RabbitListenerEndpointRegistry rabbitListenerEndpointRegistry;

    @Override
    public void handle() {
        MessageListenerContainer bookOutboxListener = rabbitListenerEndpointRegistry.getListenerContainer("bookOutboxListener");
        if(!bookOutboxListener.isRunning()) {
           bookOutboxListener.start();
           log.info("배치 서버 full reindex 완료 bookOutboxConsumerListener 재시작");
        }
    }

    @Override
    public ReindexStatus getReindexStatus() {
        return ReindexStatus.COMPLETED;
    }
}
