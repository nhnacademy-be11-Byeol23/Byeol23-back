package com.nhnacademy.byeol23backend.couponset.coupon.consumer;

import com.nhnacademy.byeol23backend.couponset.coupon.dto.BirthdayCouponIssueRequestDto;
import com.nhnacademy.byeol23backend.couponset.coupon.service.CouponService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class BirthdayCouponIssueConsumer {
    private final CouponService couponService;

    @RabbitListener(queues = "${coupon.birthday.queue}")
    public void processBirthdayIssue(BirthdayCouponIssueRequestDto request) {

        log.info("[Birthday Consumer] 메시지 수신: MemberID=" + request.memberId() + ", PolicyID=" + request.birthDateCouponPolicyId());
        couponService.issueBirthdayCoupon(request);
    }
}
