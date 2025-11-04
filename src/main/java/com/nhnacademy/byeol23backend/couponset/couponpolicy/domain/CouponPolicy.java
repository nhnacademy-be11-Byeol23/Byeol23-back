package com.nhnacademy.byeol23backend.couponset.couponpolicy.domain;

import java.math.BigDecimal;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "coupon_policy")
@NoArgsConstructor
public class CouponPolicy {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "coupon_policy_id")
	private Long couponPolicyId;

	@Column(name = "coupon_name", nullable = false)
	private String couponName;

	@Column(name = "criterion_price", nullable = false)
	private Long criterionPrice;

	@Column(name = "discount_rate")
	private Integer discountRate;

	@Column(name = "discount_limit", precision = 10)
	private BigDecimal discountLimit;

	@Column(name = "discount_amount", precision = 10)
	private BigDecimal discountAmount;

	@Column(name = "is_active", nullable = false, columnDefinition = "tinyint")
	private Boolean isActive;

	public CouponPolicy(String couponName, Long criterionPrice, Integer discountRate, BigDecimal discountLimit,
		BigDecimal discountAmount) {
		this.couponName = couponName;
		this.criterionPrice = criterionPrice;
		this.discountRate = discountRate;
		this.discountLimit = discountLimit;
		this.discountAmount = discountAmount;
	}
}