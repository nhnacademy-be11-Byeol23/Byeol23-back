package com.nhnacademy.byeol23backend.cartset.cart.domain.dto;

import com.nhnacademy.byeol23backend.cartset.cartbook.domain.dto.CartBookResponse;

import java.util.List;

public record CartResponse(
        Long cartId,
        List<CartBookResponse> cartBooks
) {}
