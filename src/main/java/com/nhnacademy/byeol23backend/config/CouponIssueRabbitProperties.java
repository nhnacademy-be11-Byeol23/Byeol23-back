package com.nhnacademy.byeol23backend.config;


import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "coupon.issue")
public record CouponIssueRabbitProperties(
        String exchange,
        String queue,
        String routingKey
) {
}
