package com.nhnacademy.byeol23backend.orderset.packaging.service.impl;

import java.util.ArrayList;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.nhnacademy.byeol23backend.image.domain.ImageDomain;
import com.nhnacademy.byeol23backend.image.dto.ImageUrlProjection;
import com.nhnacademy.byeol23backend.image.service.ImageService;
import com.nhnacademy.byeol23backend.orderset.packaging.domain.Packaging;
import com.nhnacademy.byeol23backend.orderset.packaging.domain.dto.PackagingCreateRequest;
import com.nhnacademy.byeol23backend.orderset.packaging.domain.dto.PackagingCreateResponse;
import com.nhnacademy.byeol23backend.orderset.packaging.domain.dto.PackagingInfoResponse;
import com.nhnacademy.byeol23backend.orderset.packaging.domain.dto.PackagingUpdateRequest;
import com.nhnacademy.byeol23backend.orderset.packaging.domain.dto.PackagingUpdateResponse;
import com.nhnacademy.byeol23backend.orderset.packaging.exception.PackagingNotFoundException;
import com.nhnacademy.byeol23backend.orderset.packaging.repository.PackagingRepository;
import com.nhnacademy.byeol23backend.orderset.packaging.service.PackagingService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PackagingServiceImpl implements PackagingService, ImageService {
	private final PackagingRepository packagingRepository;

	@Override
	@Transactional
	public String saveImageUrl(Long Id, String imageUrl) {
		String target = "http://storage.java21.net:8000/";
		String replacement = "https://byeol23.shop/img-proxy/";
		imageUrl = imageUrl.replace(target, replacement);
		Packaging packaging = packagingRepository.findById(Id)
			.orElseThrow(() -> new IllegalArgumentException("해당 포장재를 찾을 수 없습니다. 포장재 imageId: " + Id));
		packaging.setPackagingImgUrl(imageUrl);
		packagingRepository.save(packaging);
		return packaging.toString();
	}

	@Override
	public List<ImageUrlProjection> getImageUrlsById(Long Id) {
		String url = packagingRepository.findById(Id)
			.orElseThrow(() -> new IllegalArgumentException("해당 포장재를 찾을 수 없습니다. 포장재 imageId: " + Id))
			.getPackagingImgUrl();
		List<ImageUrlProjection> imageUrlProjections = new ArrayList<>();
		imageUrlProjections.add(new ImageUrlProjection(Id, url));
		return imageUrlProjections;
	}

	@Override
	@Transactional
	public String deleteImageUrlsById(Long Id) {
		Packaging packaging = packagingRepository.findById(Id)
			.orElseThrow(() -> new IllegalArgumentException("해당 포장재를 찾을 수 없습니다. 포장재 imageId: " + Id));
		String url = packaging.getPackagingImgUrl();
		packaging.setPackagingImgUrl(null);
		packagingRepository.save(packaging);
		return url;
	}

	@Override
	public boolean isSupportedDomain(ImageDomain imageDomain) {
		return imageDomain == ImageDomain.PACKAGING;
	}

	@Override
	public Page<PackagingInfoResponse> getAllPacakings(Pageable pageable) {
		Page<Packaging> packagings = packagingRepository.findAll(pageable);

		return packagings.map(packaging -> new PackagingInfoResponse(
			packaging.getPackagingId(),
			packaging.getPackagingName(),
			packaging.getPackagingPrice(),
			packaging.getPackagingImgUrl()
		));
	}

	@Override
	@Transactional
	public PackagingCreateResponse createPackaging(PackagingCreateRequest request) {
		Packaging packaging = Packaging.of(request.packagingName(), request.packagingPrice());

		packagingRepository.save(packaging);

		return new PackagingCreateResponse(packaging.getPackagingId(), packaging.getPackagingName(),
			packaging.getPackagingPrice(), packaging.getPackagingImgUrl());
	}

	@Override
	@Transactional
	public PackagingUpdateResponse updatePackaging(Long packagingId, PackagingUpdateRequest request) {
		Packaging packaging = packagingRepository.findById(packagingId)
			.orElseThrow(() -> new PackagingNotFoundException("해당 아이디의 포장지를 찾을 수 없습니다.: " + packagingId));

		packaging.updateInfo(request.packagingName(), request.packagingPrice());

		return new PackagingUpdateResponse(packaging.getPackagingName(), packaging.getPackagingPrice(),
			packaging.getPackagingImgUrl());
	}

	@Override
	@Transactional
	public void deletePackagingById(Long packagingId) {
		if (!packagingRepository.existsById(packagingId)) {
			throw new PackagingNotFoundException("해당 아이디의 포장지가 존재하지 않습니다.: " + packagingId);
		}

		packagingRepository.deleteById(packagingId);
	}

	@Override
	public List<PackagingInfoResponse> getPackagingLists() {
		List<Packaging> packagings = packagingRepository.findAll();

		return packagings.stream()
			.map(p -> new PackagingInfoResponse(
				p.getPackagingId(),
				p.getPackagingName(),
				p.getPackagingPrice(),
				p.getPackagingImgUrl()
			))
			.toList();
	}
}
