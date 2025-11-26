package com.nhnacademy.byeol23backend.cartset.cart.service;

import com.nhnacademy.byeol23backend.cartset.cart.dto.CustomerIdentifier;
import com.nhnacademy.byeol23backend.cartset.cartbook.dto.CartBookAddRequest;
import com.nhnacademy.byeol23backend.cartset.cartbook.dto.CartBookResponse;
import com.nhnacademy.byeol23backend.cartset.cartbook.dto.CartBookUpdateRequest;

import java.util.List;

public interface CartService {
    void addBook(CustomerIdentifier identifier, CartBookAddRequest request);
    List<CartBookResponse> getCartBooks(CustomerIdentifier identifier);
    void updateQuantity(CustomerIdentifier identifier, Long bookId, CartBookUpdateRequest request);
}
