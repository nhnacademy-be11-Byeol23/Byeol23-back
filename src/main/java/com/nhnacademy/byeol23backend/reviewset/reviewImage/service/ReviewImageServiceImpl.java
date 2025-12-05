package com.nhnacademy.byeol23backend.reviewset.reviewImage.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.nhnacademy.byeol23backend.image.domain.ImageDomain;
import com.nhnacademy.byeol23backend.image.dto.ImageUrlProjection;
import com.nhnacademy.byeol23backend.image.service.ImageService;
import com.nhnacademy.byeol23backend.reviewset.review.domain.Review;
import com.nhnacademy.byeol23backend.reviewset.review.repository.ReviewRepository;
import com.nhnacademy.byeol23backend.reviewset.reviewImage.domain.ReviewImage;
import com.nhnacademy.byeol23backend.reviewset.reviewImage.repository.ReviewImageRepository;

import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class ReviewImageServiceImpl implements ImageService {
	private final ReviewImageRepository reviewImageRepository;
	private final ReviewRepository reviewRepository;

	@Override
	public String saveImageUrl(Long Id, String imageUrl) {
		String target = "http://storage.java21.net:8000/";
		String replacement = "https://byeol23.shop/img-proxy/";
		imageUrl = imageUrl.replace(target, replacement);
		Review review = reviewRepository.findById(Id)
			.orElseThrow(() -> new IllegalArgumentException("해당 리뷰를 찾을 수 없습니다. 리뷰 imageId: " + Id));
		ReviewImage image = reviewImageRepository.save(new ReviewImage(review, imageUrl));
		return image.toString();
	}

	@Override
	public List<ImageUrlProjection> getImageUrlsById(Long Id) {
		return reviewImageRepository.findUrlsAndIdsByReviewId(Id);
	}

	@Override
	public String deleteImageUrlsById(Long Id) {
		String url = (reviewImageRepository.findById(Id)
			.orElseThrow(() -> new IllegalArgumentException("해당 리뷰 이미지를 찾을 수 없습니다. 리뷰 imageId: " + Id)))
			.getReviewImageUrl();
		reviewImageRepository.deleteById(Id);
		return url;
	}

	@Override
	public boolean isSupportedDomain(ImageDomain imageDomain) {
		return imageDomain == ImageDomain.REVIEW;
	}
}
