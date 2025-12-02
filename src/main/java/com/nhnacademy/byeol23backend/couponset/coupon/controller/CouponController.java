package com.nhnacademy.byeol23backend.couponset.coupon.controller;

import com.nhnacademy.byeol23backend.couponset.coupon.dto.*;
import com.nhnacademy.byeol23backend.couponset.coupon.service.CouponService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/coupon")
@RequiredArgsConstructor
@Slf4j
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

    /**
     * 도서 리스트를 받아와서 해당 도서 ID, 카테고리 ID를 조회
     * 카테고리 비교 : 도서 카테고리가 쿠폰 카테고리에 속하면 적용 가능
     * ex)
     * 쿠폰 : IT>프로그래밍
     * 도서1 : IT>프로그래밍>자바 =>적용가능
     * 도서2 : IT => 적용 불가능
     *
     * book 객체들
     * 객체에 있어야되는거
     * 1. 도서 id
     * 2. 카테고리 ids(루트부터 이어지는 list)
     */
    @PostMapping("/usable")
    public ResponseEntity<List<UsableCouponInfoResponse>> getUsableCoupons(
            @RequestBody List<OrderItemRequest> request,
            @CookieValue("Access-Token") String token){
        List<UsableCouponInfoResponse> usableCoupons = couponService.getUsableCoupons(token, request);

        for(UsableCouponInfoResponse couponInfoResponse : usableCoupons){
            log.info("사용가능 쿠폰 : {}", couponInfoResponse.toString());
        }
        return ResponseEntity.ok(usableCoupons);
    }

    @GetMapping("/usable-test")
    public ResponseEntity<Void> getUsableCouponsTest(
            @CookieValue("Access-Token") String token){
        couponService.getUsableCouponsTest(token);


        return ResponseEntity.ok().build();
    }

}
