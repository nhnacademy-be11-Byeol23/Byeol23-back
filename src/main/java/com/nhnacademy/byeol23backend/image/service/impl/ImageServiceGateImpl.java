package com.nhnacademy.byeol23backend.image.service.impl;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.nhnacademy.byeol23backend.bookset.bookimage.service.BookImageServiceImpl;
import com.nhnacademy.byeol23backend.image.domain.ImageDomain;
import com.nhnacademy.byeol23backend.image.dto.ImageUrlProjection;
import com.nhnacademy.byeol23backend.image.service.ImageService;
import com.nhnacademy.byeol23backend.image.service.ImageServiceGate;
import com.nhnacademy.byeol23backend.orderset.packaging.service.impl.PackagingServiceImpl;
import com.nhnacademy.byeol23backend.reviewset.reviewImage.service.ReviewImageServiceImpl;

@Service
public class ImageServiceGateImpl implements ImageServiceGate {
	private final List<ImageService> imageServices;
	public ImageServiceGateImpl(List<ImageService> imageServices) {
		this.imageServices = imageServices;
	}
	//domain에 따라 서비스 게이트가 적절한 서비스로 라우팅
	private ImageService getBookImageService(
		ImageDomain imageDomain
	) {
		return imageServices.stream()
			.filter(service -> service.isSupportedDomain(imageDomain))
			.findFirst()
			.orElseThrow(() -> new IllegalArgumentException("지원하지 않는 이미지 도메인입니다: " + imageDomain));
	}

	@Override
	public String saveImageUrl(
		Long Id,
		String imageUrl,
		ImageDomain imageDomain
	) {
		String target = "http://storage.java21.net:8000/";
		String replacement = "https://byeol23.shop/img-proxy/";
		imageUrl = imageUrl.replace(target, replacement);
		return getBookImageService(imageDomain).saveImageUrl(Id, imageUrl);
	}

	@Override
	public Map<Long, String> getImageUrlsById(
		Long Id,
		ImageDomain imageDomain
	) {
		List<ImageUrlProjection> urls = getBookImageService(imageDomain).getImageUrlsById(Id);
		return urls.stream().collect(
			Collectors.toMap(
				ImageUrlProjection::getUrlId,
				ImageUrlProjection::getImageUrl
			)
		);
	}

	@Override
	public String deleteImageUrlsById(
		Long Id,
		ImageDomain imageDomain
	) {
		return getBookImageService(imageDomain).deleteImageUrlsById(Id);
	}
}
