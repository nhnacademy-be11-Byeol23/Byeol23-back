package com.nhnacademy.byeol23backend.orderset.refundpolicy.domain;

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
@Table(name = "refund_policy")
@NoArgsConstructor
public class RefundPolicy {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "refund_policy_id")
	private Long refundPolicyId;

	@Column(name = "refund_policy_name", nullable = false, length = 30)
	private String refundPolicyName;

	@Column(name = "refund_condition", nullable = false, length = 20)
	private String refundCondition;

	@Column(name = "comment", nullable = false, columnDefinition = "text")
	private String comment;

	private LocalDateTime changedAt;

	private RefundPolicy(String refundPolicyName, String refundCondition, String comment, LocalDateTime changedAt) {
		this.refundPolicyName = refundPolicyName;
		this.refundCondition = refundCondition;
		this.comment = comment;
		this.changedAt = changedAt;
	}

	public static RefundPolicy of(String refundPolicyName, String refundCondition, String comment,
		LocalDateTime changedAt) {
		return new RefundPolicy(refundPolicyName, refundCondition, comment, changedAt);
	}
}
