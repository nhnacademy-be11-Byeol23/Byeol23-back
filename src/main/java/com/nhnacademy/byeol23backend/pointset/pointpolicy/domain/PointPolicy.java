
package com.nhnacademy.byeol23backend.pointset.pointpolicy.domain;

import com.nhnacademy.byeol23backend.pointset.pointpolicytype.domain.PointPolicyType;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Getter
@Table(name = "point_policy")
@NoArgsConstructor
public class PointPolicy implements Serializable {
	private static final long serialVersionUID = 1L;
	@Id
	@Column(name = "point_policy_id", nullable = false)
	private Long pointPolicyId;

	@JoinColumn(name = "point_policy_type", nullable = false)
	@OneToOne(fetch = FetchType.LAZY)
	private PointPolicyType pointPolicyType;

	@Column(name = "point_policy_name", nullable = false, length = 50)
	private String pointPolicyName;


	@Setter
	@Column(name = "save_amouont", nullable = false, precision = 10, scale = 0)
	private BigDecimal saveAmount;

	@Column(name = "created_at", nullable = false)
	private LocalDateTime createdAt;


//	public  PointPolicy(String pointPolicyName, BigDecimal saveAmount, Boolean isActive) {
//		this.pointPolicyName = pointPolicyName;
//		this.saveAmount = saveAmount;
//		this.createdAt = LocalDateTime.now();
//		this.isActive = isActive;
//	}

}
