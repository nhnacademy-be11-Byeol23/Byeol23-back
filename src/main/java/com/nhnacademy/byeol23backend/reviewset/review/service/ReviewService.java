package com.nhnacademy.byeol23backend.reviewset.review.service;

import java.util.List;

import com.nhnacademy.byeol23backend.reviewset.review.dto.ReviewResponse;

public interface ReviewService {
	List<ReviewResponse> getReviewsByProductId(Long bookId);
	Long registerReview(String reviewContent, Byte reviewRate, String orderNumber, Long bookId, boolean withImage);
}
