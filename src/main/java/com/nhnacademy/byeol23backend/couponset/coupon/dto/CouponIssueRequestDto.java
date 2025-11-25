package com.nhnacademy.byeol23backend.couponset.coupon.dto;

import java.time.LocalDate;

public record CouponIssueRequestDto(
        Long couponPolicyId,
        String couponName,
        LocalDate expiredDate
) {
}
