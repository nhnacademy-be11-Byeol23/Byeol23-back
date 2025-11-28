//package com.nhnacademy.byeol23backend.pointset.pointpolicy.controller;
//
//import java.util.List;
//
//import org.springframework.data.domain.Pageable;
//import org.springframework.web.bind.annotation.*;
//
//import com.nhnacademy.byeol23backend.pointset.pointpolicy.dto.PointPolicyDTO;
//import com.nhnacademy.byeol23backend.pointset.pointpolicy.service.PointPolicyService;
//
//@RestController
//@RequestMapping("/api/point-policies")
//public class PointPolicyController {
//
//	private final PointPolicyService pointPolicyService;
//
//	public PointPolicyController(PointPolicyService pointPolicyService) {
//		this.pointPolicyService = pointPolicyService;
//	}
//
//	@GetMapping
//	public List<PointPolicyDTO> getAllPointPolicies(Pageable pageable) {
//		List<PointPolicyDTO> list = pointPolicyService.getAllPointPolicies(pageable);
//		return list;
//	}
//
//	@GetMapping("/{name}")
//	public PointPolicyDTO getPointPolicy(@PathVariable("name") String name) {
//		PointPolicyDTO dto = pointPolicyService.getPointPolicy(name);
//		return dto;
//	}
//
//	@PostMapping("/create")
//	public void createPointPolicy(@RequestBody PointPolicyDTO pointPolicyDTO) {
//		pointPolicyService.savePointPolicy(pointPolicyDTO);
//	}
//
//	@PutMapping("/update")
//	public void updatePointPolicy(
//		@RequestBody PointPolicyDTO pointPolicyDTO) {
//		pointPolicyService.updatePointPolicy(pointPolicyDTO);
//	}
//
//	@DeleteMapping("/{name}")
//	public void deletePointPolicy(@PathVariable("name") String name) {
//		pointPolicyService.deletePointPolicy(name);
//	}
//}
