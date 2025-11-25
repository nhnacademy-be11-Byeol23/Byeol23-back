package com.nhnacademy.byeol23backend.couponset.coupon.dto;

import java.time.LocalDate;

public record BirthdayCouponIssueRequestDto(
        Long memberId,
        Long birthDateCouponPolicyId,
        String couponName,
        LocalDate expiredDate
) {
}
