package com.nhnacademy.byeol23backend.couponset.couponpolicy.controller;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/coupon-policy")
public class CouponPolicyController {

    @PostMapping("/create")
    public void createCouponPolicy(){

    }
}
