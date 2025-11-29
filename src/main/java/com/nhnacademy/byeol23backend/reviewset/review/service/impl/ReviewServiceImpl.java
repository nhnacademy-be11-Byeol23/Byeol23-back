package com.nhnacademy.byeol23backend.reviewset.review.service.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.nhnacademy.byeol23backend.bookset.book.dto.BookResponse;
import com.nhnacademy.byeol23backend.bookset.book.service.BookService;
import com.nhnacademy.byeol23backend.image.domain.ImageDomain;
import com.nhnacademy.byeol23backend.image.service.ImageServiceGate;
import com.nhnacademy.byeol23backend.memberset.member.domain.Member;
import com.nhnacademy.byeol23backend.memberset.member.service.MemberService;
import com.nhnacademy.byeol23backend.orderset.order.service.OrderService;
import com.nhnacademy.byeol23backend.orderset.orderdetail.domain.OrderDetail;
import com.nhnacademy.byeol23backend.orderset.orderdetail.service.OrderDetailService;
import com.nhnacademy.byeol23backend.pointset.pointhistories.service.PointService;
import com.nhnacademy.byeol23backend.pointset.pointpolicy.domain.PointPolicy;
import com.nhnacademy.byeol23backend.pointset.pointpolicy.dto.PointPolicyDTO;
import com.nhnacademy.byeol23backend.pointset.pointpolicy.dto.ReservedPolicy;
import com.nhnacademy.byeol23backend.reviewset.review.domain.Review;
import com.nhnacademy.byeol23backend.reviewset.review.dto.ReviewResponse;
import com.nhnacademy.byeol23backend.reviewset.review.repository.ReviewRepository;
import com.nhnacademy.byeol23backend.reviewset.review.service.ReviewService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class ReviewServiceImpl implements ReviewService {
	private final ReviewRepository reviewRepository;
	private final OrderDetailService orderDetailService;
	private final MemberService memberService;
	private final ImageServiceGate imageServiceGate;
	private final PointService pointService;

	@Override
	@Transactional
	public List<ReviewResponse> getReviewsByProductId(Long bookId) {
		List<Review> reviews = reviewRepository.findAllByBookId(bookId)
			.orElseThrow(()-> new IllegalArgumentException("No reviews found for bookId: " + bookId));
		List<ReviewResponse> reviewResponses = new ArrayList<>();
		for(Review review : reviews) {
			ReviewResponse response = ReviewResponse
				.builder()
				.reviewId(review.getReviewId())
				.reviewerName(review.getMember().getMemberName())
				.reviewContent(review.getReviewContent())
				.reviewRate(review.getReviewRate())
				.revisedAt(review.getCreatedAt())
				.reviewImageUrls(review.getReviewImageUrls().stream().map(v->v.getReviewImageUrl()).toList()
				)
				.build();
			reviewResponses.add(response);
		}
		return reviewResponses;
	}

	@Override
	@Transactional
	public Long registerReview(String reviewContent, Byte reviewRate, String orderNumber, Long bookId, boolean withImage) {
		OrderDetail orderDetail = orderDetailService.getOrderDetailByOrderNumberAndBookId(orderNumber, bookId);
		Long memberId = orderDetail.getOrder().getMember().getMemberId();
		Member member = memberService.getMemberProxy(memberId);
		pointService.offsetPoints(
			member,
			withImage? ReservedPolicy.REVIEW_WITH_IMAGE : ReservedPolicy.REVIEW_WITHOUT_IMAGE
		);
		Member memberProxy = memberService.getMemberProxy(memberId);
		Review review = new Review(
			reviewRate,
			reviewContent,
			memberProxy,
			orderDetail
		);
		Review saved = reviewRepository.save(review);
		return saved.getReviewId();
	}
}
