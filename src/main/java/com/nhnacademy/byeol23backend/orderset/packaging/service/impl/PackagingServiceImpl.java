package com.nhnacademy.byeol23backend.orderset.packaging.service.impl;

import java.util.ArrayList;
import java.util.List;

import com.nhnacademy.byeol23backend.image.ImageService;
import com.nhnacademy.byeol23backend.orderset.packaging.domain.Packaging;
import com.nhnacademy.byeol23backend.orderset.packaging.repository.PackagingRepository;
import com.nhnacademy.byeol23backend.orderset.packaging.service.PackagingService;
import org.springframework.stereotype.Service;

import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class PackagingServiceImpl implements PackagingService, ImageService {
	private final PackagingRepository packagingRepository;

	@Override
	public String saveImageUrl(Long Id, String imageUrl) {
		Packaging packaging = packagingRepository.findById(Id)
			.orElseThrow(() -> new IllegalArgumentException("해당 포장재를 찾을 수 없습니다. 포장재 id: " + Id));
		packaging.setPackagingImg(imageUrl);
		packagingRepository.save(packaging);
		return packaging.toString();
	}

	@Override
	public List<String> getImageUrlsById(Long Id) {
		Packaging packaging = packagingRepository.findById(Id)
			.orElseThrow(() -> new IllegalArgumentException("해당 포장재를 찾을 수 없습니다. 포장재 id: " + Id));
		String imageUrl = packaging.getPackagingImg();
		return new ArrayList<>() {{ add(imageUrl); }};
	}
}