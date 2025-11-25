package com.nhnacademy.byeol23backend.couponset.coupon.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

public record CouponInfoResponseDto(
        Long couponId,
        String couponName,
        BigDecimal discount,
        BigDecimal criterionPrice,
        LocalDate createdDate,
        LocalDate expiredDate,
        LocalDateTime usedAt,
        Boolean status
) {
}
