package com.nhnacademy.byeol23backend.reviewset.review.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.nhnacademy.byeol23backend.reviewset.review.dto.ReviewRegisterRequest;
import com.nhnacademy.byeol23backend.reviewset.review.dto.ReviewResponse;
import com.nhnacademy.byeol23backend.reviewset.review.service.ReviewService;

@RestController
@RequestMapping("/api/reviews")
public class ReviewController {
	private final ReviewService reviewService;

	public ReviewController(ReviewService reviewService) {
		this.reviewService = reviewService;
	}

	@GetMapping("/product/{book-id}")
	public ResponseEntity<List<ReviewResponse>> getReviewsByProductId(@PathVariable(
		"book-id"
	) Long bookId) {
		return ResponseEntity.ok(reviewService.getReviewsByProductId(bookId));
	}

	@PostMapping
	public ResponseEntity<Void> register(@RequestBody ReviewRegisterRequest request) {
		reviewService.registerReview(
			request.reviewContent(),
			request.reviewRate(),
			request.orderDetailId(),
			request.imageUrls()
		);
		return ResponseEntity.ok().build();
	}
}
