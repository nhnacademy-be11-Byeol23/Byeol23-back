package com.nhnacademy.byeol23backend.couponset.coupon.controller;

import com.nhnacademy.byeol23backend.couponset.coupon.dto.CouponIssueRequestDto;
import com.nhnacademy.byeol23backend.couponset.coupon.dto.IssuedCouponInfoResponseDto;
import com.nhnacademy.byeol23backend.couponset.coupon.dto.UsedCouponInfoResponseDto;
import com.nhnacademy.byeol23backend.couponset.coupon.service.CouponService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/coupon")
@RequiredArgsConstructor
public class CouponController {
    private final CouponService couponService;

    @PostMapping()
    public ResponseEntity<String> issueCoupon(@RequestBody CouponIssueRequestDto request){

        couponService.sendIssueRequestToMQ(request);

        return ResponseEntity.ok("쿠폰 발급 요청 성공");
    }

    @GetMapping("/issued")
    public ResponseEntity<List<IssuedCouponInfoResponseDto>> getIssuedCoupons(@CookieValue("Access-Token") String token){
        List<IssuedCouponInfoResponseDto> issuedCoupons = couponService.getIssuedCoupons(token);
        return ResponseEntity.ok(issuedCoupons);
    }

    @GetMapping("/used")
    public ResponseEntity<List<UsedCouponInfoResponseDto>> getUsedCoupons(@CookieValue("Access-Token") String token){
        List<UsedCouponInfoResponseDto> usedCoupons = couponService.getUsedCoupons(token);
        return ResponseEntity.ok(usedCoupons);
    }

}
