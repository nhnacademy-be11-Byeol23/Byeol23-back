package com.nhnacademy.byeol23backend.cartset.cartbook.domain.dto;

import java.math.BigDecimal;

public record CartBookResponse(
        Long cartBookId,
        Long bookId,
        String bookName,
        BigDecimal salePrice,
        int quantity
) {}