package com.nhnacademy.byeol23backend.image.impl;

import java.util.List;

import org.springframework.stereotype.Service;

import com.nhnacademy.byeol23backend.bookset.bookimage.service.BookImageServiceImpl;
import com.nhnacademy.byeol23backend.image.ImageDomain;
import com.nhnacademy.byeol23backend.image.ImageService;
import com.nhnacademy.byeol23backend.image.ImageServiceGate;
import com.nhnacademy.byeol23backend.orderset.packaging.service.impl.PackagingServiceImpl;
import com.nhnacademy.byeol23backend.reviewset.reviewImage.service.ReviewImageServiceImpl;

@Service
public class ImageServiceGateImpl implements ImageServiceGate {
	private final ImageService bookImageService;
	private final ImageService packagingService;
	private final ImageService reviewImageService;
	public ImageServiceGateImpl(
		BookImageServiceImpl bookImageServiceImpl,
		PackagingServiceImpl packagingImageServiceImpl,
		ReviewImageServiceImpl reviewImageServiceImpl
	) {
		this.bookImageService = bookImageServiceImpl;
		this.packagingService = packagingImageServiceImpl;
		this.reviewImageService = reviewImageServiceImpl;
	}
	//domain에 따라 서비스 게이트가 적절한 서비스로 라우팅
	private ImageService getBookImageService(
		ImageDomain imageDomain
	) {
		return switch (imageDomain) {
			case BOOK -> bookImageService;
			case PACKAGING -> packagingService;
			case REVIEW -> reviewImageService;
		};
	}
	@Override
	public String saveImageUrl(
		Long Id,
		String imageUrl,
		ImageDomain imageDomain
	) {
		return getBookImageService(imageDomain).saveImageUrl(Id, imageUrl);
	}

	@Override
	public List<String> getImageUrlsById(
		Long Id,
		ImageDomain imageDomain
	) {
		return getBookImageService(imageDomain).getImageUrlsById(Id);
	}
}
