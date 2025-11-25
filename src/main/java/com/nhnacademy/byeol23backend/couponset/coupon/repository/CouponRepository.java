package com.nhnacademy.byeol23backend.couponset.coupon.repository;

import com.nhnacademy.byeol23backend.couponset.coupon.domain.Coupon;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;

public interface CouponRepository extends JpaRepository<Coupon, Long> {
    @Modifying
    @Query(value = """
            INSERT INTO coupons (coupon_policy_id, coupon_name, member_id, expired_date, created_date)
            SELECT 
                :policyId,
                :couponName,
                m.member_id,
                :expiredDate,
                CURRENT_DATE()
            FROM
                members m
            WHERE 
                m.status = 'ACTIVE'; 
            """,
            nativeQuery = true)
    int issueCouponToAllUsers(
            @Param("policyId") Long policyId,
            @Param("couponName") String couponName,
            @Param("expiredDate") LocalDate expiredDate
    );

    boolean existsByMember_memberIdAndCouponPolicy_couponPolicyId(Long memberId, Long couponPolicyId);

    @Modifying
    @Query(value = """
            INSERT INTO coupons (coupon_policy_id, member_id, expired_date, created_date)
            VALUES (:policyId, :memberId, :expiredDate, CURRENT_DATE())
            """, nativeQuery = true)
    int issueBirthdayCoupon(@Param("policyId") Long policyId,
                            @Param("memberId") Long memberId,
                            @Param("expiredDate") LocalDate expiredDate);
}
