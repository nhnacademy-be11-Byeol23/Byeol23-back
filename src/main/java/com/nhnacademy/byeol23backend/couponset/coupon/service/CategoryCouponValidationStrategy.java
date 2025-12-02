package com.nhnacademy.byeol23backend.couponset.coupon.service;

import com.nhnacademy.byeol23backend.couponset.categorycoupon.repository.CategoryCouponPolicyRepository;
import com.nhnacademy.byeol23backend.couponset.coupon.domain.Coupon;
import com.nhnacademy.byeol23backend.couponset.coupon.dto.OrderContext;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class CategoryCouponValidationStrategy implements CouponValidationStrategy{

    private final CategoryCouponPolicyRepository categoryCouponPolicyRepository;

    @Override
    public boolean isApplicable(Coupon coupon, OrderContext context) {
        // 1. 쿠폰 정책 ID 추출
        Long policyId = coupon.getCouponPolicy().getCouponPolicyId();

        // 2. 쿠폰 정책이 적용되는 카테고리 ID 목록을 DB에서 조회
        //    (CategoryCouponPolicyRepository가 이 정보를 조회한다고 가정)
        List<Long> restrictedCategoryIds = categoryCouponPolicyRepository
                .findCategoryIdsByCouponPolicyId(policyId);

        // 3. 쿠폰 정책에 적용 대상 카테고리가 지정되지 않았다면 사용 불가
        if (restrictedCategoryIds == null || restrictedCategoryIds.isEmpty()) {
            return false;
        }

        // 4. 주문 컨텍스트의 카테고리 ID와 쿠폰 정책의 카테고리 ID 사이에 교집합이 있는지 확인 (핵심)
        //    주문한 상품 중 하나라도 쿠폰이 지정한 카테고리에 속하는지 확인합니다.
        return restrictedCategoryIds.stream()
                .anyMatch(restrictedId -> context.categoryIds().contains(restrictedId));
    }
}
