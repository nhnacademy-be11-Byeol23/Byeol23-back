package com.nhnacademy.byeol23backend.couponset.couponpolicy.service.impl;

import com.nhnacademy.byeol23backend.couponset.couponpolicy.domain.CouponPolicy;
import com.nhnacademy.byeol23backend.couponset.couponpolicy.domain.dto.CouponPolicyCreateRequest;
import com.nhnacademy.byeol23backend.couponset.couponpolicy.domain.dto.CouponPolicyInfoResponse;
import com.nhnacademy.byeol23backend.couponset.couponpolicy.repository.CouponPolicyRepository;
import com.nhnacademy.byeol23backend.couponset.couponpolicy.service.CouponPolicyService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
@Transactional(readOnly = true)
public class CouponPolicyServiceImpl implements CouponPolicyService {
    private final CouponPolicyRepository couponPolicyRepository;
    @Override
    @Transactional
    public void createCouponPolicy(CouponPolicyCreateRequest couponPolicyCreateRequest) {
        CouponPolicy couponPolicy = new CouponPolicy(couponPolicyCreateRequest.policyName(),
                couponPolicyCreateRequest.criterionPrice(),
                couponPolicyCreateRequest.discountRate(),
                couponPolicyCreateRequest.discountLimit(),
                couponPolicyCreateRequest.discountAmount());
        couponPolicyRepository.save(couponPolicy);
    }

    @Override
    public List<CouponPolicyInfoResponse> getCouponPolicies() {
        List<CouponPolicy> couponPolicies = couponPolicyRepository.findAll();

        return couponPolicies.stream()
                .map(couponPolicy -> new CouponPolicyInfoResponse(
                        couponPolicy.getCouponName(),
                        couponPolicy.getCriterionPrice(),
                        couponPolicy.getDiscountRate(),
                        couponPolicy.getDiscountLimit(),
                        couponPolicy.getDiscountAmount()
                ))
                .collect(Collectors.toList());
    }
}
