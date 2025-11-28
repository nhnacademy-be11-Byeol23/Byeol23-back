package com.nhnacademy.byeol23backend.pointset.activatedpointpolicy.domain;

import com.nhnacademy.byeol23backend.pointset.pointpolicy.dto.ReservedPolicy;
import jakarta.persistence.Embeddable;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Embeddable
@Getter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class ActivatedPointPolicyId {
    private Long pointPolicyId;
    @Enumerated(EnumType.STRING)
    private ReservedPolicy pointPolicyType;
}
