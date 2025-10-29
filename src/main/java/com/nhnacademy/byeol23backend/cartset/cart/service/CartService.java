package com.nhnacademy.byeol23backend.cartset.cart.service;

import com.nhnacademy.byeol23backend.cartset.cart.domain.dto.CartResponse;
import com.nhnacademy.byeol23backend.memberset.member.domain.Member;

public interface CartService {

    // 회원 장바구니 생성(회원 가입)
    void createCart(Member member);

    // 회원 아이디로 장바구니 삭제(회원 탈퇴)
    void deleteCart(Long memberId);

    // 장바구니 조회
    CartResponse getCartWithBooksByMemberId(Long memberId);

}
