package com.nhnacademy.byeol23backend.cartset.cartbook.service;

import com.nhnacademy.byeol23backend.cartset.cartbook.domain.dto.CartBookAddRequest;
import com.nhnacademy.byeol23backend.cartset.cartbook.domain.dto.CartBookUpdateRequest;

public interface CartBookService {

    // 장바구니 도서 추가
    void addCartItem(CartBookAddRequest cartBookAddRequest);

    // 장바구니 도서 개별 삭제
    void deleteCartItem(Long cartBookId);

    // 장바구니 도서 수량 업데이트
    void updateCartItemQuantity(CartBookUpdateRequest cartBookUpdateRequest);

}
