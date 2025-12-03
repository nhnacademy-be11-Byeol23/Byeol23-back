package com.nhnacademy.byeol23backend.couponset.coupon.service;

import com.nhnacademy.byeol23backend.couponset.coupon.dto.OrderContext;
import com.nhnacademy.byeol23backend.couponset.coupon.dto.OrderItemRequest;
import com.nhnacademy.byeol23backend.couponset.couponpolicy.domain.CouponPolicy;

import java.math.BigDecimal;
import java.util.List;

public interface CouponCalculationStrategy {
    BigDecimal calculateTargetSubtotal(
            CouponPolicy policy,
            List<OrderItemRequest> orderItems,
            OrderContext context
    );
}