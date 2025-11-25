package com.nhnacademy.byeol23backend.couponset.listener;

import com.nhnacademy.byeol23backend.couponset.coupon.dto.BirthdayCouponIssueRequestDto;
import com.nhnacademy.byeol23backend.couponset.coupon.dto.WelcomeCouponIssueEvent;
import com.nhnacademy.byeol23backend.couponset.coupon.service.CouponService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

import java.time.LocalDate;

@Component
@RequiredArgsConstructor
public class CouponIssueEventListener {
    private final CouponService couponService;

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handleWelcomeCouponEvent(WelcomeCouponIssueEvent event) {

        BirthdayCouponIssueRequestDto request = new BirthdayCouponIssueRequestDto(
                event.memberId(),
                event.welcomeCouponPolicyId(),
                event.welcomeCouponName(),
                LocalDate.now().plusDays(30) // 만료일 설정
        );

        // 2. MQ로 전송 (비동기 처리 시작)
        couponService.sendBirthdayIssueRequestToMQ(request);

        // 3. 리스너는 즉시 종료됩니다. (회원가입 API 응답 지연 방지)
    }
}