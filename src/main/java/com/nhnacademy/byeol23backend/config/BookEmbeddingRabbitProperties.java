package com.nhnacademy.byeol23backend.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "book.embedding")
public record BookEmbeddingRabbitProperties(String exchange, String queue, String routingKey) {
}
