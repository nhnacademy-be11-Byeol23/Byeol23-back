package com.nhnacademy.byeol23backend.couponset.couponpolicy.service;

import com.nhnacademy.byeol23backend.couponset.couponpolicy.domain.dto.CouponPolicyCreateRequest;
import com.nhnacademy.byeol23backend.couponset.couponpolicy.domain.dto.CouponPolicyInfoResponse;

import java.util.List;

public interface CouponPolicyService {
    void createCouponPolicy(CouponPolicyCreateRequest couponPolicyCreateRequest);

    List<CouponPolicyInfoResponse> getCouponPolicies();
}
