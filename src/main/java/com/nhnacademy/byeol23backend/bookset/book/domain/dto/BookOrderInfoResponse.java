package com.nhnacademy.byeol23backend.bookset.book.domain.dto;

import java.math.BigDecimal;

public record BookOrderInfoResponse(String bookTitle,
									int quantity,
									BigDecimal price) {
}