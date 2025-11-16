package com.nhnacademy.byeol23backend.config;

import lombok.RequiredArgsConstructor;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.annotation.EnableRabbit;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableRabbit
@RequiredArgsConstructor
@EnableConfigurationProperties(value = {BookOutboxRabbitProperties.class, BookReindexRabbitProperties.class})
public class RabbitMqConfig {
    private final BookOutboxRabbitProperties bookOutboxRabbitProperties;
    private final BookReindexRabbitProperties bookReindexRabbitProperties;

    @Bean
    public DirectExchange bookOutboxExchange() {
        return new DirectExchange(bookOutboxRabbitProperties.exchange());
    }

    @Bean
    public Queue bookOutboxQueue() {
        return new Queue(bookOutboxRabbitProperties.queue(), true);
    }

    @Bean
    public Binding bookOutboxBinding() {
        return BindingBuilder
                .bind(bookOutboxQueue())
                .to(bookOutboxExchange())
                .with(bookOutboxRabbitProperties.routingKey());
    }

    @Bean
    public DirectExchange bookReindexExchange() {
        return new DirectExchange(bookReindexRabbitProperties.exchange());
    }

    @Bean
    public Queue bookReindexQueue() {
        return new Queue(bookReindexRabbitProperties.queue(), true);
    }

    @Bean
    public Binding bookReindexBinding() {
        return BindingBuilder
                .bind(bookReindexQueue())
                .to(bookReindexExchange())
                .with(bookReindexRabbitProperties.routingKey());
    }
}
