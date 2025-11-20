package com.nhnacademy.byeol23backend.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "book.outbox")
public record BookOutboxRabbitProperties(String exchange, String queue, String routingKey) {
}
