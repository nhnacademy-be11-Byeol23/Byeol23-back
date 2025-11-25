package com.nhnacademy.byeol23backend.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "coupon.birthday")
public record CouponBirthdayRabbitProperties(
        String queue,
        String routingKey
) {
}