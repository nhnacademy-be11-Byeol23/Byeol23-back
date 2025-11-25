package com.nhnacademy.byeol23backend.couponset.coupon.dto;

public record WelcomeCouponIssueEvent(
        Long memberId,
        Long welcomeCouponPolicyId,
        String welcomeCouponName
) {
}
