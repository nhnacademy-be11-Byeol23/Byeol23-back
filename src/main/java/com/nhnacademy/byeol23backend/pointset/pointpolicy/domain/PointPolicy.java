package com.nhnacademy.byeol23backend.pointset.pointpolicy.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import org.hibernate.annotations.Immutable;

@Entity
@Getter
@Table(name = "point_policy")
@NoArgsConstructor
@Immutable
public class PointPolicy {
	@Id
	@Column(name = "point_policy_name", nullable = false, length = 50)
	private String pointPolicyName;

	@Column(name = "save_amouont", nullable = false, precision = 10, scale = 0)
	private BigDecimal saveAmount;

	@Column(name = "created_at", nullable = false)
	private LocalDateTime createdAt;

	@Column(name = "is_active", nullable = false, columnDefinition = "tinyint(1)")
	private Boolean isActive;

	public  PointPolicy(String pointPolicyName, BigDecimal saveAmount, Boolean isActive) {
		this.pointPolicyName = pointPolicyName;
		this.saveAmount = saveAmount;
		this.createdAt = LocalDateTime.now();
		this.isActive = isActive;
	}

}