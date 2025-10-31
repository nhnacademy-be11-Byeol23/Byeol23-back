package com.nhnacademy.byeol23backend.image;

public interface ImageServiceGate {
	//minio에 업로드 된 url을 db에 저장
	String saveImageUrl(Long Id, String imageUrl, ImageDomain imageDomain);
	//id로 이미지 url 조회
	java.util.List<String> getImageUrlsById(Long Id, ImageDomain imageDomain);
}
