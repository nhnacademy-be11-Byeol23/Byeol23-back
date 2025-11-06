package com.nhnacademy.byeol23backend.couponset.couponpolicy.controller;

import com.nhnacademy.byeol23backend.couponset.couponpolicy.domain.dto.CouponPolicyCreateRequest;
import com.nhnacademy.byeol23backend.couponset.couponpolicy.domain.dto.CouponPolicyInfoResponse;
import com.nhnacademy.byeol23backend.couponset.couponpolicy.service.CouponPolicyService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/coupon-policy")
public class CouponPolicyController {
    private final CouponPolicyService couponPolicyService;

    @PostMapping("/create")
    public ResponseEntity<Void> createCouponPolicy(@RequestBody CouponPolicyCreateRequest couponPolicyCreateRequest){
        couponPolicyService.createCouponPolicy(couponPolicyCreateRequest);
        return ResponseEntity.created(null).build();
    }

    @GetMapping
    public ResponseEntity<List<CouponPolicyInfoResponse>> getCouponPolicies(){
        List<CouponPolicyInfoResponse> couponPolicies = couponPolicyService.getCouponPolicies();
        return ResponseEntity.ok().body(couponPolicies);
    }
}
