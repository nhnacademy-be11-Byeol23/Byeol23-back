package com.nhnacademy.byeol23backend.orderset.delivery.domain;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@Table(name = "delivery_policy")
@NoArgsConstructor
public class DeliveryPolicy {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "delivery_policy_id")
	private Long deliveryPolicyId;

	@Column(name = "free_delivery_condition", nullable = false, precision = 10)
	private BigDecimal freeDeliveryCondition;

	@Column(name = "delivery_fee", nullable = false, precision = 10)
	private BigDecimal deliveryFee;

	private LocalDateTime changedAt;

	public DeliveryPolicy(BigDecimal freeDeliveryCondition, BigDecimal deliveryFee, LocalDateTime changedAt) {
		this.freeDeliveryCondition = freeDeliveryCondition;
		this.deliveryFee = deliveryFee;
		this.changedAt = changedAt;
	}
}
