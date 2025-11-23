package com.nhnacademy.byeol23backend.couponset.coupon.service.impl;

import com.nhnacademy.byeol23backend.config.CouponIssueRabbitProperties;
import com.nhnacademy.byeol23backend.couponset.coupon.dto.CouponIssueRequestDto;
import com.nhnacademy.byeol23backend.couponset.coupon.service.CouponService;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
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
