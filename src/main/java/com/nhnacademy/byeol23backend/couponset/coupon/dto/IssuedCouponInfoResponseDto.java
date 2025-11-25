package com.nhnacademy.byeol23backend.couponset.coupon.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

public record IssuedCouponInfoResponseDto(
        Long couponPolicyId,
        Long couponId,
        String couponName,
        String discount,
        BigDecimal criterionPrice,
        LocalDate createdDate,
        LocalDate expiredDate,
        Boolean status
) {
}
