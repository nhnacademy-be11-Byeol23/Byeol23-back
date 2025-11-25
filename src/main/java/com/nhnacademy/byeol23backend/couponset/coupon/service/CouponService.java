package com.nhnacademy.byeol23backend.couponset.coupon.service;

import com.nhnacademy.byeol23backend.couponset.coupon.dto.CouponIssueRequestDto;

import java.time.LocalDate;

public interface CouponService {
    void sendIssueRequestToMQ(CouponIssueRequestDto request);

    void issueCoupon(CouponIssueRequestDto request);
}
