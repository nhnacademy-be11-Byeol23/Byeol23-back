package com.nhnacademy.byeol23backend.couponset.coupon.service;

import com.nhnacademy.byeol23backend.couponset.bookcoupon.repository.BookCouponRepository;
import com.nhnacademy.byeol23backend.couponset.coupon.domain.Coupon;
import com.nhnacademy.byeol23backend.couponset.coupon.dto.OrderContext;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class BookCouponValidationStrategy implements CouponValidationStrategy {
    private final BookCouponRepository bookCouponRepository;


    @Override
    public boolean isApplicable(Coupon coupon, OrderContext context) {
        Long couponPolicyId = coupon.getCouponPolicy().getCouponPolicyId();

        // 1. 해당 정책이 적용되는 모든 도서 ID를 DB에서 조회
        List<Long> restrictedBookIds = bookCouponRepository.findBookIdsByCouponPolicyId(couponPolicyId);

        // 2. 현재 주문 목록(context.getBookIds())과 교집합이 있는지 확인
        return restrictedBookIds.stream()
                .anyMatch(context.bookIds()::contains);
    }
}