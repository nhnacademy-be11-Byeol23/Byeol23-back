package com.nhnacademy.byeol23backend.couponset.couponpolicy.service.impl;

import com.nhnacademy.byeol23backend.couponset.couponpolicy.domain.CouponPolicy;
import com.nhnacademy.byeol23backend.couponset.couponpolicy.domain.dto.CouponPolicyCreateRequest;
import com.nhnacademy.byeol23backend.couponset.couponpolicy.repository.CouponPolicyRepository;
import com.nhnacademy.byeol23backend.couponset.couponpolicy.service.CouponPolicyService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class CouponPolicyServiceImpl implements CouponPolicyService {
    private final CouponPolicyRepository couponPolicyRepository;
    @Override
    public void createCouponPolicy(CouponPolicyCreateRequest couponPolicyCreateRequest) {
        CouponPolicy couponPolicy = new CouponPolicy(couponPolicyCreateRequest.policyName(),
                couponPolicyCreateRequest.criterionPrice(),
                couponPolicyCreateRequest.discountRate(),
                couponPolicyCreateRequest.discountLimit(),
                couponPolicyCreateRequest.discountAmount());
        couponPolicyRepository.save(couponPolicy);
    }
}
