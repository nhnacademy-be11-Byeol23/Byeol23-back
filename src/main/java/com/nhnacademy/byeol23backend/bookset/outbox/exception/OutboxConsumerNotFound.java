package com.nhnacademy.byeol23backend.bookset.outbox.exception;

import com.nhnacademy.byeol23backend.bookset.outbox.consumer.ReindexStatus;

public class OutboxConsumerNotFound extends RuntimeException {
    public OutboxConsumerNotFound(ReindexStatus status) {
        super("%s에 맞는 OutboxConsumer 인스턴스를 찾을 수 없습니다.".formatted(status.name()));
    }
}
