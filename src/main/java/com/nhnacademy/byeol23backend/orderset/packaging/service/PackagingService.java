package com.nhnacademy.byeol23backend.orderset.packaging.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.nhnacademy.byeol23backend.orderset.packaging.domain.dto.PackagingCreateRequest;
import com.nhnacademy.byeol23backend.orderset.packaging.domain.dto.PackagingCreateResponse;
import com.nhnacademy.byeol23backend.orderset.packaging.domain.dto.PackagingInfoResponse;
import com.nhnacademy.byeol23backend.orderset.packaging.domain.dto.PackagingUpdateRequest;
import com.nhnacademy.byeol23backend.orderset.packaging.domain.dto.PackagingUpdateResponse;

public interface PackagingService {
	Page<PackagingInfoResponse> getAllPacakings(Pageable pageable);

	PackagingCreateResponse createPackaging(PackagingCreateRequest request);

	PackagingUpdateResponse updatePackaging(Long packagingId, PackagingUpdateRequest request);

	void deletePackagingById(Long packagingId);
}
