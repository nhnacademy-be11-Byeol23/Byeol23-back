package com.nhnacademy.byeol23backend.bookset.outbox.publisher;

import com.nhnacademy.byeol23backend.bookset.outbox.event.BookOutboxEvent;
import com.nhnacademy.byeol23backend.config.BookOutboxRabbitProperties;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Slf4j
@Component
@RequiredArgsConstructor
public class BookOutboxPublisher {
    private final RabbitTemplate rabbitTemplate;
    private final BookOutboxRabbitProperties bookOutboxRabbitProperties;

    @Async("ioExecutor")
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handleBookOutboxAddEvent(BookOutboxEvent event) {
        Long outboxId = event.outboxId();
        rabbitTemplate.convertAndSend(bookOutboxRabbitProperties.exchange(), bookOutboxRabbitProperties.routingKey(), outboxId);

        log.info("book outbox 이벤트 발행: {}", outboxId);
    }
}
