package com.nhnacademy.byeol23backend.pointset.pointpolicy.controller;

import java.util.List;
import java.util.Map;

import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.nhnacademy.byeol23backend.pointset.activatedpointpolicy.service.ActivatedPointPolicyService;
import com.nhnacademy.byeol23backend.pointset.pointpolicy.dto.PointPolicyDTO;
import com.nhnacademy.byeol23backend.pointset.pointpolicy.dto.ReservedPolicy;
import com.nhnacademy.byeol23backend.pointset.pointpolicy.service.PointPolicyService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping("/api/point-policies")
@RequiredArgsConstructor
public class PointPolicyController {
	private final PointPolicyService pointPolicyService;
	private final ActivatedPointPolicyService activatedPointPolicyService;

	@GetMapping
	public ResponseEntity<Map<ReservedPolicy, List<PointPolicyDTO>>> getAllPointPolicies() {
		Map<ReservedPolicy, List<PointPolicyDTO>> pointPolicies = pointPolicyService.getAllPointPolicies();
		return ResponseEntity.ok(pointPolicies);
	}

	@PostMapping
	public ResponseEntity<Void> createPointPolicy(@RequestBody PointPolicyDTO pointPolicyDTO) {
		pointPolicyService.savePointPolicy(pointPolicyDTO);
		return ResponseEntity.status(HttpStatus.CREATED).build();
	}

	@GetMapping("/{id}")
	public ResponseEntity<PointPolicyDTO> getPointPolicyById(@PathVariable("id") Long id) {
		PointPolicyDTO dto = pointPolicyService.getPointPolicyById(id);
		return ResponseEntity.ok(dto);
	}

	@PostMapping("update/{id}")
	public ResponseEntity<Void> updatePointPolicy(@PathVariable Long id, @RequestBody PointPolicyDTO pointPolicyDTO) {
		// 경로 id를 DTO의 id로 보장하여 서비스 호출
		PointPolicyDTO dtoWithId = new PointPolicyDTO(
			pointPolicyDTO.type(),
			id,
			pointPolicyDTO.pointPolicyName(),
			pointPolicyDTO.saveAmount(),
			pointPolicyDTO.isActive(),
			pointPolicyDTO.createdAt()
		);
		pointPolicyService.updatePointPolicy(dtoWithId);
		return ResponseEntity.noContent().build();
	}

	@PostMapping("delete/{id}")
	public ResponseEntity<Void> deletePointPolicy(@PathVariable Long id) {
		pointPolicyService.deletePointPolicy(id);
		return ResponseEntity.noContent().build();
	}
	@PostMapping("activate/{id}")
	public ResponseEntity<Void> activatePointPolicy(@PathVariable Long id) {
		activatedPointPolicyService.activatePolicy(id);
		return ResponseEntity.noContent().build();
	}
}
