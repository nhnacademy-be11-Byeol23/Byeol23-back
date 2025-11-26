package com.nhnacademy.byeol23backend.bookset.book.domain.dto;

import java.math.BigDecimal;

import com.nhnacademy.byeol23backend.orderset.packaging.domain.dto.PackagingInfoResponse;

public record BookOrderInfoResponse(Long bookId,
									String bookTitle,
									int quantity,
									BigDecimal price,
									PackagingInfoResponse packaging) {
}