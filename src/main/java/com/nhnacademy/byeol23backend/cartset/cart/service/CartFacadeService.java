package com.nhnacademy.byeol23backend.cartset.cart.service;

import com.nhnacademy.byeol23backend.cartset.cart.dto.CustomerIdentifier;
import com.nhnacademy.byeol23backend.cartset.cart.service.impl.GuestCartService;
import com.nhnacademy.byeol23backend.cartset.cart.service.impl.MemberCartService;
import com.nhnacademy.byeol23backend.cartset.cartbook.dto.CartBookAddRequest;
import com.nhnacademy.byeol23backend.cartset.cartbook.dto.CartBookResponse;
import com.nhnacademy.byeol23backend.cartset.cartbook.dto.CartBookUpdateRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CartFacadeService {
    private final MemberCartService memberCartService;
    private final GuestCartService guestCartService;

    public void addBook(CustomerIdentifier identifier, CartBookAddRequest request) {
        if(identifier.memberId() != null) memberCartService.addBook(identifier, request);
        else guestCartService.addBook(identifier, request);
    }

    public List<CartBookResponse> getCartBooks(CustomerIdentifier identifier) {
        if(identifier.memberId() != null) return memberCartService.getCartBooks(identifier);
        else return guestCartService.getCartBooks(identifier);
    }

    public void updateCartBook(CustomerIdentifier identifier, Long bookId, CartBookUpdateRequest request) {
        if(identifier.memberId() != null) memberCartService.updateQuantity(identifier, bookId, request);
        else guestCartService.updateQuantity(identifier, bookId, request);
    }
}
