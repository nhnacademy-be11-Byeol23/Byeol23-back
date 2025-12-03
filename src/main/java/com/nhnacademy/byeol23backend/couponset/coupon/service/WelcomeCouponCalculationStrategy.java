package com.nhnacademy.byeol23backend.couponset.coupon.service;

import com.nhnacademy.byeol23backend.couponset.coupon.dto.OrderContext;
import com.nhnacademy.byeol23backend.couponset.coupon.dto.OrderItemRequest;
import com.nhnacademy.byeol23backend.couponset.couponpolicy.domain.CouponPolicy;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.List;

@Component
public class WelcomeCouponCalculationStrategy implements CouponCalculationStrategy {
    @Override
    public BigDecimal calculateTargetSubtotal(CouponPolicy policy, List<OrderItemRequest> items, OrderContext context) {
        // WELCOME 정책: 전체 주문 금액이 대상
        return context.totalPrice();
    }
}