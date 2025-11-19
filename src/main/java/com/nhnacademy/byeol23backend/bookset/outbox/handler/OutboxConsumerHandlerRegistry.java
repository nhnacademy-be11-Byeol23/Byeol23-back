package com.nhnacademy.byeol23backend.bookset.outbox.handler;

import com.nhnacademy.byeol23backend.bookset.outbox.consumer.ReindexStatus;
import com.nhnacademy.byeol23backend.bookset.outbox.exception.OutboxConsumerNotFound;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class OutboxConsumerHandlerRegistry {
    private final List<OutboxConsumerHandler> handlers;

    public OutboxConsumerHandler getOutboxConsumerHandler(ReindexStatus status) {
        return handlers.stream()
                .filter(handler -> handler.getReindexStatus() == status)
                .findFirst()
                .orElseThrow(() -> new OutboxConsumerNotFound(status));

    }
}
