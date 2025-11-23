package com.nhnacademy.byeol23backend.couponset.coupon.service.impl;

import com.nhnacademy.byeol23backend.config.CouponIssueRabbitProperties;
import com.nhnacademy.byeol23backend.couponset.coupon.dto.CouponIssueRequestDto;
import com.nhnacademy.byeol23backend.couponset.coupon.service.CouponService;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CouponServiceImpl implements CouponService {
    private final RabbitTemplate rabbitTemplate;
    private final CouponIssueRabbitProperties properties;

    public void sendIssueRequestToMQ(CouponIssueRequestDto request) {
        rabbitTemplate.convertAndSend(
                properties.exchange(),
                properties.routingKey(),
                request
        );
    }
}
