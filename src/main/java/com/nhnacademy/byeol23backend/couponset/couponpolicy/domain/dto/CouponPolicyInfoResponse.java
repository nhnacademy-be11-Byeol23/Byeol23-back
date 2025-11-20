package com.nhnacademy.byeol23backend.couponset.couponpolicy.domain.dto;

import java.math.BigDecimal;

public record CouponPolicyInfoResponse(
        Long couponPolicyId,
        String policyName,
        BigDecimal criterionPrice,
        Integer discountRate,
        BigDecimal discountLimit,
        BigDecimal discountAmount,
        String couponPolicyType
) {
}
