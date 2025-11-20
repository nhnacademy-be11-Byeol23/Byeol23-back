package com.nhnacademy.byeol23backend.bookset.outbox;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@Entity
@Table(name = "book_outbox")
@NoArgsConstructor
public class BookOutbox {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "aggregate_id")
    private Long aggregateId;

    @Enumerated(EnumType.STRING)
    @Column(name = "event_type")
    private EventType eventType;

    @Column(name = "occurred_at")
    private LocalDateTime occurredAt;

    @Column(name = "processed")
    private boolean processed;

    @Column(name = "processed_at")
    private LocalDateTime processedAt;

    public BookOutbox(Long aggregateId, EventType eventType) {
        this.aggregateId = aggregateId;
        this.eventType = eventType;
        occurredAt = LocalDateTime.now();
        processed = false;
        processedAt = null;
    }

    public void markAsProcessed() {
        processed = true;
        processedAt = LocalDateTime.now();
    }

    public enum EventType {
        ADD, UPDATE, UPDATE_IMAGE, DELETE;
    }
}
