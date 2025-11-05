package com.nhnacademy.byeol23backend.pointset.pointpolicy.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.time.LocalDateTime;

@Entity
@Table(name = "point_policy")
public class PointPolicy {
    @Id
    @Column(name = "point_policy_id")
    private Integer pointPolicyId;

    @Column(name = "point_policy_type", nullable = false, length = 20)
    private String pointPolicyType;

    @Column(name = "point_policy_name", nullable = false, length = 50)
    private String pointPolicyName;

    @Column(name = "save_amouont", nullable = false)
    private Integer saveAmount;

    private LocalDateTime createdAt;

    @Column(name = "is_active", nullable = false, columnDefinition = "tinyint(1)")
    private Boolean isActive;
}
