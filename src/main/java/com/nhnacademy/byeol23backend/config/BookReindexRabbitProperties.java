package com.nhnacademy.byeol23backend.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "book.reindex")
public record BookReindexRabbitProperties(String exchange, String queue, String routingKey) {
}
