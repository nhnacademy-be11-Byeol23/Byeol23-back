package com.nhnacademy.byeol23backend.cartset.cartbook.domain.dto;

public record CartBookAddRequest(Long cartId, Long bookId, int quantity) {}
