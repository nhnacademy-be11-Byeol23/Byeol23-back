package com.nhnacademy.byeol23backend.cartset.cart.controller;

import com.nhnacademy.byeol23backend.cartset.cart.domain.dto.CartResponse;
import com.nhnacademy.byeol23backend.cartset.cart.service.CartService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/carts")
@RequiredArgsConstructor
public class CartController {
    private final CartService cartService;

    // memberId로 장바구니 조회
    @GetMapping("/{memberId}")
    public CartResponse getCartByMember(@PathVariable Long memberId) {
        return cartService.getCartWithBooksByMemberId(memberId);
    }

}
