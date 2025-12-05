package com.nhnacademy.byeol23backend.couponset.categorycoupon.repository;

import com.nhnacademy.byeol23backend.couponset.categorycoupon.domain.CategoryCouponPolicy;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface CategoryCouponPolicyRepository extends JpaRepository<CategoryCouponPolicy, Long> {
    @Query("SELECT c.category.categoryId FROM CategoryCouponPolicy c WHERE c.couponPolicy.couponPolicyId = :policyId")
    List<Long> findCategoryIdsByCouponPolicyId(@Param("policyId") Long policyId);
}
