package com.nhnacademy.byeol23backend.reviewset.review.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.nhnacademy.byeol23backend.memberset.member.domain.Member;
import com.nhnacademy.byeol23backend.memberset.member.service.MemberService;
import com.nhnacademy.byeol23backend.orderset.orderdetail.domain.OrderDetail;
import com.nhnacademy.byeol23backend.orderset.orderdetail.service.OrderDetailService;
import com.nhnacademy.byeol23backend.reviewset.review.dto.ReviewRegisterRequest;
import com.nhnacademy.byeol23backend.reviewset.review.dto.ReviewResponse;
import com.nhnacademy.byeol23backend.reviewset.review.service.ReviewService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/reviews")
@RequiredArgsConstructor
public class ReviewController {
	private final ReviewService reviewService;
	private final OrderDetailService orderDetailService;
	private final MemberService memberService;

	@GetMapping("/product/{book-id}")
	public ResponseEntity<List<ReviewResponse>> getReviewsByProductId(@PathVariable(
		"book-id"
	) Long bookId) {
		return ResponseEntity.ok(reviewService.getReviewsByProductId(bookId));
	}

	@PostMapping
	public ResponseEntity<Long> register(@RequestBody ReviewRegisterRequest request) {

		Long id = reviewService.registerReview(
			request.reviewContent(),
			request.reviewRate(),
			request.orderDetailId(),
			request.imageUrls()
		);
		return ResponseEntity.ok(id);
	}

	@GetMapping("/reviewable")
	public ResponseEntity<List<OrderDetail>> getReviewableOrderDetails() {
		//TODO: 제대로도니 Member 갖고오기
		Long memberId = 1L; // 임시로 1L로 설정
		List<OrderDetail> reviewableOrderDetails = orderDetailService.getReviewableOrderDetailsByMemberId(memberId);
		return ResponseEntity.ok(reviewableOrderDetails);
	}


}
