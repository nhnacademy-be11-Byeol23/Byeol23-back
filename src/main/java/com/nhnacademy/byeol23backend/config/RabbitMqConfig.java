package com.nhnacademy.byeol23backend.config;

import lombok.RequiredArgsConstructor;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.annotation.EnableRabbit;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableRabbit
@RequiredArgsConstructor
@EnableConfigurationProperties(value = {BookOutboxRabbitProperties.class, BookReindexRabbitProperties.class, BookEmbeddingRabbitProperties.class})
public class RabbitMqConfig {
    private final BookOutboxRabbitProperties bookOutboxRabbitProperties;
    private final BookReindexRabbitProperties bookReindexRabbitProperties;
    private final BookEmbeddingRabbitProperties bookEmbeddingRabbitProperties;

    @Bean
    public MessageConverter messageConverter() {
        return new Jackson2JsonMessageConverter();
    }

    @Bean
    public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory, MessageConverter messageConverter) {
        RabbitTemplate rabbitTemplate = new RabbitTemplate(connectionFactory);
        rabbitTemplate.setMessageConverter(messageConverter);
        return rabbitTemplate;
    }

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

    @Bean
    public DirectExchange bookEmbeddingExchange() {
        return new DirectExchange(bookEmbeddingRabbitProperties.exchange());
    }

    @Bean
    public Queue bookEmbeddingQueue() {
        return new Queue(bookEmbeddingRabbitProperties.queue(), true);
    }

    @Bean
    public Binding bookEmbeddingBinding() {
        return BindingBuilder
                .bind(bookEmbeddingQueue())
                .to(bookReindexExchange())
                .with(bookEmbeddingRabbitProperties.routingKey());
    }
}
