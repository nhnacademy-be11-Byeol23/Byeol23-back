package com.nhnacademy.byeol23backend.couponset.coupon.consumer;

import com.nhnacademy.byeol23backend.couponset.coupon.dto.CouponIssueRequestDto;
import com.nhnacademy.byeol23backend.couponset.coupon.service.CouponService;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.AmqpRejectAndDontRequeueException;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CouponIssueConsumer {
    private final CouponService couponService;

    @RabbitListener(queues = "${coupon.issue.queue}")
    public void processCouponIssueRequest(CouponIssueRequestDto request) {

        try {
            couponService.issueCoupon(request);

        } catch (Exception e) {
            throw new AmqpRejectAndDontRequeueException("영구적 오류로 판단, 메시지를 DLQ로 보냄");
        }
    }
}