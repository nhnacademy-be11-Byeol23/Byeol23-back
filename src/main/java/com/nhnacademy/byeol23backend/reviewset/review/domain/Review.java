package com.nhnacademy.byeol23backend.reviewset.review.domain;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import com.nhnacademy.byeol23backend.memberset.member.domain.Member;
import com.nhnacademy.byeol23backend.orderset.orderdetail.domain.OrderDetail;
import com.nhnacademy.byeol23backend.pointset.pointhistories.domain.PointHistory;
import com.nhnacademy.byeol23backend.reviewset.reviewImage.domain.ReviewImage;


import jakarta.persistence.*;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.Getter;
import lombok.Setter;

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
  @Column(name = "review_rate")
  private Byte reviewRate;

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

	@OneToOne
	@JoinColumn(name = "point_history_id", nullable = true)
	@Setter
	private PointHistory pointHistory;
}
