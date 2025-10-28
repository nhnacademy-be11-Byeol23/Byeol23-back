package com.nhnacademy.byeol23backend.reviewset.review.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.nhnacademy.byeol23backend.reviewset.review.domain.Review;

public interface ReviewRepository extends JpaRepository<Review, Long> {
}
