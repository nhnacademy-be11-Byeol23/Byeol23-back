package com.nhnacademy.byeol23backend.orderset.refundpolicy.domain;

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

	@Column(name = "refund_option", nullable = false, length = 30)
	private RefundOption refundOption;

	private RefundPolicy(RefundOption refundOption) {
		this.refundOption = refundOption;
	}

	public static RefundPolicy of(RefundOption refundOption) {
		return new RefundPolicy(refundOption);
	}

}
