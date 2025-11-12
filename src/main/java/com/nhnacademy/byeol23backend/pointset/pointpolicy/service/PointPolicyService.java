package com.nhnacademy.byeol23backend.pointset.pointpolicy.service;

import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.nhnacademy.byeol23backend.pointset.pointpolicy.domain.PointPolicy;
import com.nhnacademy.byeol23backend.pointset.pointpolicy.dto.PointPolicyDTO;
import com.nhnacademy.byeol23backend.pointset.pointpolicy.repository.PointPolicyRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class PointPolicyService {
	private final PointPolicyRepository pointPolicyRepository;

	public List<PointPolicyDTO> getAllPointPolicies(Pageable pageable) {
		return pointPolicyRepository.findAll(pageable)
			.map(pointPolicy -> new PointPolicyDTO(
				pointPolicy.getPointPolicyName(),
				pointPolicy.getSaveAmount(),
				pointPolicy.getIsActive(),
				pointPolicy.getCreatedAt()
			))
			.getContent();
	}

	public PointPolicyDTO getPointPolicy(String name) {
		PointPolicy pointPolicy = pointPolicyRepository.findByPointPolicyName(name)
			.orElseThrow(() -> new RuntimeException("PointPolicy not found with name: " + name));

		return new PointPolicyDTO(
			pointPolicy.getPointPolicyName(),
			pointPolicy.getSaveAmount(),
			pointPolicy.getIsActive(),
			pointPolicy.getCreatedAt()
		);
	}
	@Transactional
	public void savePointPolicy(PointPolicyDTO pointPolicyDTO) {
		PointPolicy pointPolicy = new PointPolicy(
			pointPolicyDTO.pointPolicyName(),
			pointPolicyDTO.saveAmount(),
			true
		);
		pointPolicyRepository.save(pointPolicy);
	}
	@Transactional
	public void deletePointPolicy(String name) {
		pointPolicyRepository.deleteByPointPolicyName(name);
	}

	@Transactional
	public void updatePointPolicy(PointPolicyDTO pointPolicyDTO) {
		PointPolicy existingPolicy = pointPolicyRepository.findByPointPolicyName(pointPolicyDTO.pointPolicyName())
			.orElseThrow(() -> new RuntimeException("PointPolicy not found with name: " + pointPolicyDTO.pointPolicyName()));
		if (pointPolicyDTO.saveAmount() != null) {
			existingPolicy.setSaveAmount(pointPolicyDTO.saveAmount());
		}
		if (pointPolicyDTO.isActive() != null) {
			existingPolicy.setIsActive(pointPolicyDTO.isActive());
		}
		pointPolicyRepository.save(existingPolicy);
	}
}
