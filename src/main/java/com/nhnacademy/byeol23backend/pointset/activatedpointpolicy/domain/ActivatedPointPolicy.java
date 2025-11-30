package com.nhnacademy.byeol23backend.pointset.activatedpointpolicy.domain;

import com.nhnacademy.byeol23backend.pointset.pointpolicy.domain.PointPolicy;
import com.nhnacademy.byeol23backend.pointset.pointpolicytype.domain.PointPolicyType;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "activated_point_policy")
@NoArgsConstructor
@Getter
public class ActivatedPointPolicy {

    @EmbeddedId
    private ActivatedPointPolicyId id = new ActivatedPointPolicyId();

    @MapsId("pointPolicyId")
    @OneToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "point_policy_id")
    private PointPolicy pointPolicy;

    @MapsId("pointPolicyType")
    @OneToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "point_policy_type")
    private PointPolicyType pointPolicyType;

	public ActivatedPointPolicy(PointPolicy pointPolicy) {
		this.pointPolicy = pointPolicy;
		this.pointPolicyType = pointPolicy.getPointPolicyType();
		this.id = new ActivatedPointPolicyId(pointPolicy.getPointPolicyId(), pointPolicy.getPointPolicyType()
			.getPointPolicyType());
	}
}
