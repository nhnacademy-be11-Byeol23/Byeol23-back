package com.nhnacademy.byeol23backend.couponset.coupon.service;

import com.nhnacademy.byeol23backend.couponset.coupon.domain.Coupon;
import com.nhnacademy.byeol23backend.couponset.coupon.dto.OrderContext;

public interface CouponValidationStrategy {
    boolean isApplicable(Coupon coupon, OrderContext context);
}