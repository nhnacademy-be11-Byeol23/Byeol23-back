package com.nhnacademy.byeol23backend.pointset.pointpolicy.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.nhnacademy.byeol23backend.pointset.pointpolicy.domain.PointPolicy;

public interface PointPolicyRepository extends JpaRepository<PointPolicy, Integer> {
}

