package com.nhnacademy.byeol23backend.image.service;

import java.util.List;

import com.nhnacademy.byeol23backend.image.domain.ImageDomain;
import com.nhnacademy.byeol23backend.image.dto.ImageUrlProjection;

public interface ImageService {
	//minio에 업로드 된 url을 db에 저장
	String saveImageUrl(Long Id, String imageUrl);

	//id로 이미지 url 조회
	List<ImageUrlProjection> getImageUrlsById(Long Id);

	void deleteImageUrlsById(Long Id);

	ImageDomain getImageDomain();

	default boolean validate(ImageDomain imageDomain) {
		return getImageDomain().equals(imageDomain);
	}
}
