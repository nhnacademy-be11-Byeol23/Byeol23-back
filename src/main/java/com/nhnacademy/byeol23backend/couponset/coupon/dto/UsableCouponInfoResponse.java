package com.nhnacademy.byeol23backend.couponset.coupon.dto;

import com.nhnacademy.byeol23backend.couponset.coupon.domain.Coupon;
import com.nhnacademy.byeol23backend.couponset.couponpolicy.domain.CouponPolicy;
import java.math.BigDecimal;
import java.time.LocalDate;

public record UsableCouponInfoResponse(
        Long couponId,
        String couponName,
        String discountType,
        BigDecimal discountValue,
        BigDecimal criterionPrice,
        BigDecimal discountLimit,
        String policyTargetType,
        LocalDate expiredDate
) {
    public static UsableCouponInfoResponse fromEntity(Coupon coupon) {

        CouponPolicy policy = coupon.getCouponPolicy();

        String discountType;
        BigDecimal discountValue;

        if (policy.getDiscountRate() != null) {
            BigDecimal rate = new BigDecimal(policy.getDiscountRate());
            discountType = "RATE";
            discountValue = rate;

        } else if (policy.getDiscountAmount() != null) {
            discountType = "FIXED";
            discountValue = policy.getDiscountAmount();

        } else {
            discountType = "NONE";
            discountValue = BigDecimal.ZERO;
        }

        return new UsableCouponInfoResponse(
                coupon.getCouponId(),
                policy.getCouponPolicyName(),
                discountType,
                discountValue,
                policy.getCriterionPrice(),
                policy.getDiscountLimit(),
                policy.getCouponPolicyType().getValue(),
                coupon.getExpiredDate()
        );
    }
}