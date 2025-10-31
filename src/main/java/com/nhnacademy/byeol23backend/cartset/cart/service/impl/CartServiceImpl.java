package com.nhnacademy.byeol23backend.cartset.cart.service.impl;

import com.nhnacademy.byeol23backend.cartset.cart.domain.Cart;
import com.nhnacademy.byeol23backend.cartset.cart.domain.dto.CartResponse;
import com.nhnacademy.byeol23backend.cartset.cart.exception.CartNotFoundException;
import com.nhnacademy.byeol23backend.cartset.cart.repository.CartRepository;
import com.nhnacademy.byeol23backend.cartset.cart.service.CartService;
import com.nhnacademy.byeol23backend.cartset.cartbook.domain.CartBook;
import com.nhnacademy.byeol23backend.cartset.cartbook.domain.dto.CartBookResponse;
import com.nhnacademy.byeol23backend.memberset.member.domain.Member;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class CartServiceImpl implements CartService {

    private final CartRepository cartRepository;

    @Override
    public void createCart(Member member) {
        Cart cart = new Cart();
        cart.setMember(member);
        cartRepository.save(cart);
    }

    @Override
    public void deleteCart(Long memberId) {
        cartRepository.deleteByMember_MemberId(memberId);
    }

    @Override
    @Transactional(readOnly = true)
    public CartResponse getCartWithBooksByMemberId(Long memberId) {
        Cart cart = cartRepository.findCartWithBooksByMemberId(memberId)
                .orElseThrow(() -> new CartNotFoundException("장바구니가 존재하지 않습니다: " + memberId));

        List<CartBookResponse> bookResponses = new ArrayList<>();

        for (CartBook cb: cart.getCartBooks()) {
            CartBookResponse response = new CartBookResponse(
                    cb.getCartBookId(),
                    cb.getBook().getBookId(),
                    cb.getBook().getBookName(),
                    cb.getBook().getSalePrice(),
                    cb.getQuantity()
            );
            bookResponses.add(response);
        }

        return new CartResponse(cart.getCartId(), bookResponses);
    }

}
