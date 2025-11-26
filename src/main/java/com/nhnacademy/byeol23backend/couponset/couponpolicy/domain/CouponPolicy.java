package com.nhnacademy.byeol23backend.couponset.couponpolicy.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Entity
@Getter
@Table(name = "coupon_policy")
@NoArgsConstructor
public class CouponPolicy {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "coupon_policy_id")
    private Long couponPolicyId;

    @Column(name = "coupon_policy_name", nullable = false, length = 30)
    private String couponPolicyName;

    @Column(name = "criterion_price", nullable = false, precision = 10)
    private BigDecimal criterionPrice;

    @Column(name = "discount_rate")
    private Integer discountRate;

    @Column(name = "discount_limit", precision = 10)
    private BigDecimal discountLimit;

    @Column(name = "discount_amount", precision = 10)
    private BigDecimal discountAmount;

    @Column(name = "is_active", nullable = false)
    private Boolean isActive;

    @Column(name = "coupon_policy_type", nullable = false)
    @Convert(converter = CouponPolicyTypeConverter.class)
    private CouponPolicyType couponPolicyType;

    public CouponPolicy(String couponPolicyName, BigDecimal criterionPrice, Integer discountRate,
                        BigDecimal discountLimit, BigDecimal discountAmount, CouponPolicyType couponPolicyType) {
        this.couponPolicyName = couponPolicyName;
        this.criterionPrice = criterionPrice;
        this.discountRate = discountRate;
        this.discountLimit = discountLimit;
        this.discountAmount = discountAmount;
        this.isActive = true;
        this.couponPolicyType = couponPolicyType;
    }
}