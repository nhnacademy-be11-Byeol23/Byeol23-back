package com.nhnacademy.byeol23backend.cartset.cart.dto;

public record CustomerIdentifier(Long memberId, String guestId) {
    public static CustomerIdentifier member(Long memberId) {
        return new CustomerIdentifier(memberId, null);
    }
    public static CustomerIdentifier guest(String guestId) {
        return new CustomerIdentifier(null, guestId);
    }
}
