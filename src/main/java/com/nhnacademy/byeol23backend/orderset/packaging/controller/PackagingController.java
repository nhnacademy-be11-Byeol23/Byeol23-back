package com.nhnacademy.byeol23backend.orderset.packaging.controller;

import java.net.URI;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.nhnacademy.byeol23backend.orderset.packaging.domain.dto.PackagingCreateRequest;
import com.nhnacademy.byeol23backend.orderset.packaging.domain.dto.PackagingCreateResponse;
import com.nhnacademy.byeol23backend.orderset.packaging.domain.dto.PackagingInfoResponse;
import com.nhnacademy.byeol23backend.orderset.packaging.domain.dto.PackagingUpdateRequest;
import com.nhnacademy.byeol23backend.orderset.packaging.domain.dto.PackagingUpdateResponse;
import com.nhnacademy.byeol23backend.orderset.packaging.service.PackagingService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping("/api/packagings")
@RequiredArgsConstructor
public class PackagingController {
	private final PackagingService packagingService;

	@GetMapping
	public ResponseEntity<Page<PackagingInfoResponse>> getAllPackagings(@PageableDefault(size = 10) Pageable pageable) {
		Page<PackagingInfoResponse> response = packagingService.getAllPacakings(pageable);
		return ResponseEntity.ok(response);
	}

	@PostMapping
	public ResponseEntity<PackagingCreateResponse> createPackaging(@RequestBody PackagingCreateRequest request) {
		PackagingCreateResponse response = packagingService.createPackaging(request);
		URI uri = URI.create("/api/packagings/" + response.packagingId());
		return ResponseEntity.created(uri).body(response);
	}

	@PostMapping("/{packaging-id}/update")
	public ResponseEntity<PackagingUpdateResponse> updatePackaging(
		@PathVariable(name = "packaging-id") Long packagingId, @RequestBody PackagingUpdateRequest request) {
		PackagingUpdateResponse response = packagingService.updatePackaging(packagingId, request);
		return ResponseEntity.ok(response);
	}

	@PostMapping("/{packaging-id}/delete")
	public ResponseEntity<Void> deleteById(@PathVariable(name = "packaging-id") Long packagingId) {
		packagingService.deletePackagingById(packagingId);
		return ResponseEntity.noContent().build();
	}

	@GetMapping("/lists")
	public List<PackagingInfoResponse> getAllPackagingLists() {
		return packagingService.getPackagingLists();
	}

}
