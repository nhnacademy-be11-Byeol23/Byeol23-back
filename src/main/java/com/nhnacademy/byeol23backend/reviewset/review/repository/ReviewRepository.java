package com.nhnacademy.byeol23backend.reviewset.review.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.nhnacademy.byeol23backend.reviewset.review.domain.Review;

public interface ReviewRepository extends JpaRepository<Review, Long> {
	@Query("SELECT b FROM Review b JOIN FETCH b.reviewImageUrls WHERE b.reviewId = :id")
	Optional<Review> findWithImageById(Long id);
}
