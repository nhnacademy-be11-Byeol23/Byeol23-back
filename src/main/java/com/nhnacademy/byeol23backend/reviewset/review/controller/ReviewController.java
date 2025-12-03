package com.nhnacademy.byeol23backend.reviewset.review.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.nhnacademy.byeol23backend.memberset.member.domain.Member;
import com.nhnacademy.byeol23backend.memberset.member.service.MemberService;
import com.nhnacademy.byeol23backend.orderset.order.service.OrderService;
import com.nhnacademy.byeol23backend.orderset.orderdetail.domain.OrderDetail;
import com.nhnacademy.byeol23backend.orderset.orderdetail.service.OrderDetailService;
import com.nhnacademy.byeol23backend.reviewset.review.dto.ReviewRegisterRequest;
import com.nhnacademy.byeol23backend.reviewset.review.dto.ReviewResponse;
import com.nhnacademy.byeol23backend.reviewset.review.service.ReviewService;
import com.nhnacademy.byeol23backend.utils.JwtParser;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/reviews")
@RequiredArgsConstructor
public class ReviewController {
	private final ReviewService reviewService;
	private final OrderDetailService orderDetailService;
	private final MemberService memberService;
	private final JwtParser jwtParser;

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
			request.orderNumber(),
			request.bookId(),
			request.withImage()
		);
		return ResponseEntity.ok(id);
	}

	@GetMapping("/reviewable")
	public ResponseEntity<List<OrderDetail>> getReviewableOrderDetails(@CookieValue(name = "Access-Token") String accessToken) {
		Long memberId = jwtParser.parseToken(accessToken).get("memberId",Long.class);
		List<OrderDetail> reviewableOrderDetails = orderDetailService.getReviewableOrderDetailsByMemberId(memberId);
		return ResponseEntity.ok(reviewableOrderDetails);
	}

}
