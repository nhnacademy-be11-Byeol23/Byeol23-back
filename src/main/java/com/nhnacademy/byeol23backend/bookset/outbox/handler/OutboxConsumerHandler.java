package com.nhnacademy.byeol23backend.bookset.outbox.handler;

import com.nhnacademy.byeol23backend.bookset.outbox.consumer.ReindexStatus;

public interface OutboxConsumerHandler {
    void handle();
    ReindexStatus getReindexStatus();
}
