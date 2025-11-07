package com.nhnacademy.byeol23backend.reviewset.reviewImage.domain;

import com.nhnacademy.byeol23backend.reviewset.review.domain.Review;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "review_images")
@NoArgsConstructor
@Getter
public class ReviewImage {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "review_image_id")
	private Long reviewImageId;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "review_id", nullable = false)
	private Review review;

	@Column(name = "review_image_url", nullable = false)
	private String reviewImageUrl;

	public ReviewImage(Review review, String reviewImageUrl) {
		this.review = review;
		this.reviewImageUrl = reviewImageUrl;
	}

}
