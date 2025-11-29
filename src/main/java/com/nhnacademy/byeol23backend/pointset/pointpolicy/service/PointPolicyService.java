package com.nhnacademy.byeol23backend.pointset.pointpolicy.service;

import java.awt.print.Pageable;
import java.util.List;
import java.util.Map;

import com.nhnacademy.byeol23backend.pointset.pointpolicy.dto.PointPolicyDTO;
import com.nhnacademy.byeol23backend.pointset.pointpolicy.dto.ReservedPolicy;

public interface PointPolicyService {
	public Map<ReservedPolicy, List<PointPolicyDTO>> getAllPointPolicies();
	public void savePointPolicy(PointPolicyDTO pointPolicyDTO);
	public void deletePointPolicy(Long id);
	public void updatePointPolicy(PointPolicyDTO pointPolicyDTO);
}
