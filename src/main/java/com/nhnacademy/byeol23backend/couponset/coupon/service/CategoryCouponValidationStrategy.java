package com.nhnacademy.byeol23backend.couponset.coupon.service;

import com.nhnacademy.byeol23backend.couponset.coupon.domain.Coupon;
import com.nhnacademy.byeol23backend.couponset.coupon.dto.OrderContext;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CategoryCouponValidationStrategy implements CouponValidationStrategy{

    @Override
    public boolean isApplicable(Coupon coupon, OrderContext context) {




        return false;
    }
}
