package com.nhnacademy.byeol23backend.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "coupon.bulk")
public record CouponBulkRabbitProperties(
        String queue,
        String routingKey
) {
}