package com.nhnacademy.byeol23backend.couponset.coupon.service;

import com.nhnacademy.byeol23backend.couponset.coupon.domain.Coupon;
import com.nhnacademy.byeol23backend.couponset.coupon.dto.OrderContext;
import org.springframework.stereotype.Component;

/**
 * 웰컴쿠폰은 모든 상품에 적용 가능
 */
@Component
public class WelcomeCouponValidationStrategy implements CouponValidationStrategy {
    @Override
    public boolean isApplicable(Coupon coupon, OrderContext context) {
        return true;
    }
}