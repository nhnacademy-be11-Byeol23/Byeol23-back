package com.nhnacademy.byeol23backend.couponset.coupon.dto;

import java.math.BigDecimal;
import java.util.List;

public record OrderContext(
        List<Long> bookIds,
        List<Long> categoryIds,
        Long totalPrice
) {
}