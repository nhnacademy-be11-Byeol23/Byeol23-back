package com.nhnacademy.byeol23backend.couponset.coupon.service;

import com.nhnacademy.byeol23backend.couponset.coupon.dto.CouponIssueRequestDto;

public interface CouponService {
    void sendIssueRequestToMQ(CouponIssueRequestDto request);
}
