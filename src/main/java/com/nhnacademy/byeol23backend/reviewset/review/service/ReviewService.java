package com.nhnacademy.byeol23backend.reviewset.review.service;

import java.util.List;

import com.nhnacademy.byeol23backend.reviewset.review.dto.ReviewResponse;

public interface ReviewService {
	List<ReviewResponse> getReviewsByProductId(Long bookId);
	void registerReview(String reviewContent, Byte reviewRate, Long orderDetailId, List<String> imageUrls);

}
