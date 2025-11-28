package com.nhnacademy.byeol23backend.pointset.pointpolicytype.domain;

import com.nhnacademy.byeol23backend.pointset.pointpolicy.dto.ReservedPolicy;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "point_policy_type")
@NoArgsConstructor
@Getter
public class PointPolicyType {
    @Id
    @Enumerated(EnumType.STRING)
    @Column(name = "point_policy_type", length = 20)
    private ReservedPolicy pointPolicyType;
}
