package com.nhnacademy.byeol23backend.reviewset.review.dto;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

import com.nhnacademy.byeol23backend.reviewset.reviewImage.domain.ReviewImage;

import lombok.Builder;

@Builder
public record ReviewResponse(
	Long reviewId,
	String reviewerName,
	Byte reviewRate,
	String reviewContent,
	LocalDateTime revisedAt,
	List<String> reviewImageUrls
) {
}
