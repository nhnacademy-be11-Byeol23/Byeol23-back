package com.nhnacademy.byeol23backend.reviewset.review.domain;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import com.nhnacademy.byeol23backend.memberset.member.domain.Member;
import com.nhnacademy.byeol23backend.orderset.orderdetail.domain.OrderDetail;
import com.nhnacademy.byeol23backend.reviewset.reviewImage.domain.ReviewImage;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.Getter;

@Getter
@Entity
@Table(name = "reviews")
public class Review {
	@Id
	@Column(name = "review_id")
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long reviewId;

	@Min(1)
	@Max(5)
	@Column(name = "review_rate", columnDefinition = "tinyint")
	private Integer reviewRate;

	@Column(name = "review_content", columnDefinition = "text")
	private String reviewContent;

	//    @Column(name = "review_image")
	//    private String reviewImage;

	private LocalDateTime createdAt;

	private LocalDateTime revisedAt;

	@JoinColumn(name = "member_id", nullable = false)
	@ManyToOne(fetch = FetchType.LAZY)
	private Member member;

	@JoinColumn(name = "order_detail_id", nullable = false)
	@OneToOne(fetch = FetchType.LAZY)
	private OrderDetail orderDetail;

	@OneToMany(mappedBy = "review", fetch = FetchType.LAZY, orphanRemoval = true)
	private List<ReviewImage> reviewImageUrls = new ArrayList<>();

}
