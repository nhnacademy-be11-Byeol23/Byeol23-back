package com.nhnacademy.byeol23backend.reviewset.reviewImage.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.nhnacademy.byeol23backend.image.ImageService;
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
		Review review = reviewRepository.findById(Id)
			.orElseThrow(() -> new IllegalArgumentException("해당 리뷰를 찾을 수 없습니다. 리뷰 id: " + Id));
		ReviewImage image = reviewImageRepository.save(new ReviewImage(review, imageUrl));
		return image.toString();
	}

	@Override
	public List<String> getImageUrlsById(Long Id) {
		Review review = reviewRepository.findWithImageById(Id)
			.orElseThrow(() -> new IllegalArgumentException("해당 리뷰를 찾을 수 없습니다. 리뷰 id: " + Id));
		return review.getReviewImageUrls().stream()
			.map(ReviewImage::getReviewImageUrl)
			.toList();
	}
}
