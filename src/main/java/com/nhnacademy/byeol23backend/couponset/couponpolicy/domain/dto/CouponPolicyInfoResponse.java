package com.nhnacademy.byeol23backend.couponset.couponpolicy.domain.dto;

import java.math.BigDecimal;

public record CouponPolicyInfoResponse(
        String policyName,
        Long criterionPrice,
        Integer discountRate,
        BigDecimal discountLimit,
        BigDecimal discountAmount
) {
}
