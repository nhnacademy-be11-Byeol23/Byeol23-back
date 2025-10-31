package com.nhnacademy.byeol23backend.cartset.cartbook.controller;

import com.nhnacademy.byeol23backend.cartset.cartbook.domain.dto.CartBookAddRequest;
import com.nhnacademy.byeol23backend.cartset.cartbook.domain.dto.CartBookUpdateRequest;
import com.nhnacademy.byeol23backend.cartset.cartbook.service.CartBookService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/cart-books")
@RequiredArgsConstructor
public class CartBookController {
    private final CartBookService cartBookService;

    // 장바구니 도서 추가
    @PostMapping
    public void addCartBook(@RequestBody CartBookAddRequest request) {
        cartBookService.addCartItem(request);
    }

    // 장바구니 도서 수량 수정
    @PutMapping
    public void updateCartBook(@RequestBody CartBookUpdateRequest request) {
        cartBookService.updateCartItemQuantity(request);
    }

    // 장바구니 도서 삭제
    @DeleteMapping("/{cartBookId}")
    public void deleteCartBook(@PathVariable Long cartBookId) {
        cartBookService.deleteCartItem(cartBookId);
    }
}
