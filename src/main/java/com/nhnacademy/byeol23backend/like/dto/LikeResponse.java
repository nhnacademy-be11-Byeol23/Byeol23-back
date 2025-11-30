package com.nhnacademy.byeol23backend.like.dto;

import java.math.BigDecimal;

public record LikeResponse(
	Long bookId,
	String bookName,
	String imageUrl,
	String publisherName,
	String contributors,
	BigDecimal regularPrice,
	BigDecimal salePrice,
	Long reviewCount,
	BigDecimal ratingAverage
) {
}

