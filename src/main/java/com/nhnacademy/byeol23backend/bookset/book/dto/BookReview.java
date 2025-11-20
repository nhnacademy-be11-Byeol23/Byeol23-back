package com.nhnacademy.byeol23backend.bookset.book.dto;

import java.math.BigDecimal;

public record BookReview(Long bookId, Long reviewCount, BigDecimal ratingAverage) {
}
