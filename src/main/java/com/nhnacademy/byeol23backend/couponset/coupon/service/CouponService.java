package com.nhnacademy.byeol23backend.couponset.coupon.service;

import com.nhnacademy.byeol23backend.couponset.coupon.dto.*;
import com.nhnacademy.byeol23backend.couponset.couponpolicy.domain.CouponPolicy;

import java.math.BigDecimal;
import java.util.List;

public interface CouponService {
    void sendIssueRequestToMQ(CouponIssueRequestDto request);

    void sendBirthdayIssueRequestToMQ(BirthdayCouponIssueRequestDto request);

    void issueCoupon(CouponIssueRequestDto request);

    void issueBirthdayCoupon(BirthdayCouponIssueRequestDto request);

    List<IssuedCouponInfoResponseDto> getIssuedCoupons(String token);

    List<UsedCouponInfoResponseDto> getUsedCoupons(String token);

    List<UsableCouponInfoResponse> getUsableCoupons(String token, List<OrderItemRequest> request);

    Long calculateFinalDiscount(CouponApplyRequest request);

    Long calculateTotalAmount(List<OrderItemRequest> orderItems);

    Long calculateDiscountValue(CouponPolicy policy, BigDecimal targetSubtotal);
}
