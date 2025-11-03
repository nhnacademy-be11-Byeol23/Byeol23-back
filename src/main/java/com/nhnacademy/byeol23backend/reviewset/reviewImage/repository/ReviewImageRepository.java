package com.nhnacademy.byeol23backend.reviewset.reviewImage.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.nhnacademy.byeol23backend.image.dto.ImageUrlProjection;
import com.nhnacademy.byeol23backend.reviewset.reviewImage.domain.ReviewImage;

public interface ReviewImageRepository extends JpaRepository<ReviewImage, Long> {
	@Query(
		"select new com.nhnacademy.byeol23backend.image.dto.ImageUrlProjection(ri.reviewImageId, ri.reviewImageUrl) " +
			"from ReviewImage ri " +
			"where ri.review.reviewId = :Id"
	)
	List<ImageUrlProjection> findUrlsAndIdsByReviewId(Long Id);
}
