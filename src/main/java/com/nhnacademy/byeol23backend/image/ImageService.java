package com.nhnacademy.byeol23backend.image;

import java.util.List;

public interface ImageService {
	//minio에 업로드 된 url을 db에 저장
	String saveImageUrl(Long Id, String imageUrl);
	//id로 이미지 url 조회
	List<String> getImageUrlsById(Long Id);
}
