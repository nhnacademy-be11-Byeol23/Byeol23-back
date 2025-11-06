package com.nhnacademy.byeol23backend.image.service;

import java.util.Map;

import com.nhnacademy.byeol23backend.image.domain.ImageDomain;

public interface ImageServiceGate {
	//minio에 업로드 된 url을 db에 저장
	String saveImageUrl(Long Id, String imageUrl, ImageDomain imageDomain);
	//id로 이미지 url 조회
	Map<Long, String> getImageUrlsById(Long Id, ImageDomain imageDomain);

	void deleteImageUrlsById(Long Id, ImageDomain imageDomain);
}
