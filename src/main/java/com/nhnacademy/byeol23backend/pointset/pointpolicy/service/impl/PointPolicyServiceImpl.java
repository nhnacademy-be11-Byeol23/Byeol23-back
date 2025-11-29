package com.nhnacademy.byeol23backend.pointset.pointpolicy.service.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.nhnacademy.byeol23backend.pointset.activatedpointpolicy.domain.ActivatedPointPolicy;
import com.nhnacademy.byeol23backend.pointset.pointpolicy.domain.PointPolicy;
import com.nhnacademy.byeol23backend.pointset.pointpolicy.dto.PointPolicyDTO;
import com.nhnacademy.byeol23backend.pointset.pointpolicy.dto.ReservedPolicy;
import com.nhnacademy.byeol23backend.pointset.pointpolicy.repository.PointPolicyRepository;
import com.nhnacademy.byeol23backend.pointset.pointpolicy.service.PointPolicyService;
import com.nhnacademy.byeol23backend.pointset.pointpolicytype.domain.PointPolicyType;
import com.nhnacademy.byeol23backend.pointset.pointpolicytype.repository.PointPolicyTypeRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class PointPolicyServiceImpl implements PointPolicyService {
	private final PointPolicyRepository pointPolicyRepository;
	private final PointPolicyTypeRepository pointPolicyTypeRepository;

	@Override
	public Map<ReservedPolicy, List<PointPolicyDTO>> getAllPointPolicies() { //관리자페이지에서만 사용할것이므로 pageable 생략
		Map<ReservedPolicy, List<PointPolicyDTO>> result = new HashMap<>();
		List<PointPolicyType> pointPolicyTypes = pointPolicyTypeRepository.findAllWithPointPolicies();

		for (PointPolicyType type : pointPolicyTypes) {
			List<PointPolicyDTO> dtoList = new ArrayList<>();//각 타입별 dto리스트 생성
			ActivatedPointPolicy activatedPointPolicy = type.getActivatedPointPolicy();
			Long activatedPolicyId = null; //활성화된 정책이 없을 수도 있음
			if(activatedPointPolicy != null){
				activatedPolicyId = activatedPointPolicy.getPointPolicy().getPointPolicyId();
			}
			for (PointPolicy policy : type.getPointPolicies()) {//parsing
				PointPolicyDTO dto = new PointPolicyDTO(
					policy.getPointPolicyType().getPointPolicyType(),
					policy.getPointPolicyId(),
					policy.getPointPolicyName(),
					policy.getSaveAmount(),
					policy.getPointPolicyId().equals(activatedPolicyId),
					policy.getCreatedAt()
				);
				dtoList.add(dto);
			}
			result.put(type.getPointPolicyType(), dtoList);
		}
		return result;
	}

	@Override
	public void savePointPolicy(PointPolicyDTO pointPolicyDTO) {
		PointPolicyType policyType = pointPolicyTypeRepository.findById(pointPolicyDTO.type())
			.orElseThrow(() -> new RuntimeException("PointPolicyType not found: " + pointPolicyDTO.type()));

		PointPolicy pointPolicy = new PointPolicy(
			pointPolicyDTO.pointPolicyName(),
			pointPolicyDTO.saveAmount(),
			policyType
		);

		pointPolicyRepository.save(pointPolicy);
	}

	@Override
	public void deletePointPolicy(Long id) {
		pointPolicyRepository.deleteById(id);
	}

	@Override
	@Transactional
	public void updatePointPolicy(PointPolicyDTO pointPolicyDTO) { //이름과 금액만 수정 가능 TODO 활성화는 따로
		PointPolicy existingPolicy = pointPolicyRepository.findById(pointPolicyDTO.pointPolicyId())
			.orElseThrow(() -> new RuntimeException("PointPolicy not found with id: " + pointPolicyDTO.pointPolicyId()));

		if (pointPolicyDTO.saveAmount() != null) {
			existingPolicy.setSaveAmount(pointPolicyDTO.saveAmount());
		}
		if (pointPolicyDTO.pointPolicyName() != null) {
			existingPolicy.setPointPolicyName(pointPolicyDTO.pointPolicyName());
		}
		pointPolicyRepository.save(existingPolicy);
	}

	@Override
	public PointPolicyDTO getPointPolicyById(Long id) {
		PointPolicy policy = pointPolicyRepository.findById(id)
			.orElseThrow(() -> new RuntimeException("PointPolicy not found with id: " + id));

		return new PointPolicyDTO(
			policy.getPointPolicyType().getPointPolicyType(),
			policy.getPointPolicyId(),
			policy.getPointPolicyName(),
			policy.getSaveAmount(),
			null, // 활성화 여부는 여기서 판단하지 않음
			policy.getCreatedAt()
		);
	}
}
