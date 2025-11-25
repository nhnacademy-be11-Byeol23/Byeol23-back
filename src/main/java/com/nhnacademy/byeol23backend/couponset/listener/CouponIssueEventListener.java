package com.nhnacademy.byeol23backend.couponset.listener;

import com.nhnacademy.byeol23backend.couponset.coupon.dto.BirthdayCouponIssueRequestDto;
import com.nhnacademy.byeol23backend.couponset.coupon.service.CouponService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Component
@RequiredArgsConstructor
public class CouponIssueEventListener {
    private final CouponService couponService;

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handleWelcomeCouponEvent(BirthdayCouponIssueRequestDto event) {

        // MQ도 그대로 사용
        couponService.sendBirthdayIssueRequestToMQ(event);

    }
}