package com.nhnacademy.byeol23backend.bookset.outbox.manager;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.listener.RabbitListenerEndpointRegistry;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class BookOutboxListenerManager {
    private static final String FULL_REINDEX_KEY = "batch:full-reindex:lock";
    private static final String LISTENER_ID = "bookOutboxListener";

    private final RabbitListenerEndpointRegistry listenerEndpointRegistry;
    private final StringRedisTemplate stringRedisTemplate;
}
