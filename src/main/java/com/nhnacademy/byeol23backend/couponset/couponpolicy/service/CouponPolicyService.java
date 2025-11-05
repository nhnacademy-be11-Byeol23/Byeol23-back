package com.nhnacademy.byeol23backend.couponset.couponpolicy.service;

import com.nhnacademy.byeol23backend.couponset.couponpolicy.domain.dto.CouponPolicyCreateRequest;

public interface CouponPolicyService {
    void createCouponPolicy(CouponPolicyCreateRequest couponPolicyCreateRequest);
}
