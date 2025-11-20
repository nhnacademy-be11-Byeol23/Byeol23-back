package com.nhnacademy.byeol23backend.couponset.couponpolicy.service;

import com.nhnacademy.byeol23backend.couponset.couponpolicy.domain.dto.CouponPolicyCreateRequest;
import com.nhnacademy.byeol23backend.couponset.couponpolicy.domain.dto.CouponPolicyInfoResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface CouponPolicyService {
    void createCouponPolicy(CouponPolicyCreateRequest couponPolicyCreateRequest);

    Page<CouponPolicyInfoResponse> getCouponPolicies(Pageable pageable);
}
