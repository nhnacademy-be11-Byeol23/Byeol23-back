package com.nhnacademy.byeol23backend.reviewset.review.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.nhnacademy.byeol23backend.reviewset.review.domain.Review;

public interface ReviewRepository extends JpaRepository<Review, Long> {
	@Query("SELECT b FROM Review b JOIN FETCH b.reviewImageUrls WHERE b.reviewId = :id")
	Optional<Review> findWithImageById(Long id);


	@Query("SELECT r FROM Review r "
		+ "JOIN FETCH r.member "  // <--- 핵심: Member를 즉시 로딩해서 가져옴
		+ "LEFT JOIN FETCH r.reviewImageUrls " // <--- 핵심: ReviewImage들을 즉시 로딩해서 가져옴
		+ "JOIN r.orderDetail "   // OrderDetail은 조건절용으로만 쓴다면 일반 JOIN 유지
		+ "WHERE r.orderDetail.book.bookId = :bookId") // book 객체 비교가 아니라면 id 명시 추천
	Optional<List<Review>> findAllByBookId(@Param("bookId") Long bookId);
}
