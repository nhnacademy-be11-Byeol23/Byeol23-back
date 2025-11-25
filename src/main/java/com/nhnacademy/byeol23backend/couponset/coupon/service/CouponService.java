package com.nhnacademy.byeol23backend.couponset.coupon.service;

import com.nhnacademy.byeol23backend.couponset.coupon.dto.BirthdayCouponIssueRequestDto;
import com.nhnacademy.byeol23backend.couponset.coupon.dto.CouponIssueRequestDto;
import com.nhnacademy.byeol23backend.couponset.coupon.dto.IssuedCouponInfoResponseDto;

import java.util.List;

public interface CouponService {
    void sendIssueRequestToMQ(CouponIssueRequestDto request);

    void sendBirthdayIssueRequestToMQ(BirthdayCouponIssueRequestDto request);

    void issueCoupon(CouponIssueRequestDto request);

    void issueBirthdayCoupon(BirthdayCouponIssueRequestDto request);

    List<IssuedCouponInfoResponseDto> getIssuedCoupons(String token);
}
