package com.nhnacademy.byeol23backend.couponset.couponpolicy.controller;

import com.nhnacademy.byeol23backend.couponset.couponpolicy.domain.dto.CouponPolicyCreateRequest;
import com.nhnacademy.byeol23backend.couponset.couponpolicy.service.CouponPolicyService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/coupon-policy")
public class CouponPolicyController {
    private final CouponPolicyService couponPolicyService;

    @PostMapping("/create")
    public void createCouponPolicy(CouponPolicyCreateRequest couponPolicyCreateRequest){
        couponPolicyService.createCouponPolicy(couponPolicyCreateRequest);
    }
}
