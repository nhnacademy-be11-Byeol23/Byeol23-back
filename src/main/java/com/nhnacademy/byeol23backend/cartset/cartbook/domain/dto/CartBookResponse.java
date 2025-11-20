package com.nhnacademy.byeol23backend.cartset.cartbook.domain.dto;

import com.nhnacademy.byeol23backend.bookset.contributor.domain.dto.AllContributorResponse;
import com.nhnacademy.byeol23backend.bookset.publisher.domain.dto.AllPublishersInfoResponse;

import java.math.BigDecimal;
import java.util.List;

public record CartBookResponse(
        Long cartBookId,
        Long bookId,
        String bookName,
        String imageUrl,
        boolean isPack,
        BigDecimal regularPrice,
        BigDecimal salePrice,
        AllPublishersInfoResponse publisher,
        int quantity,
        List<AllContributorResponse> contributors,
        Long packagingId
) {}