package com.nhnacademy.byeol23backend.cartset.cart.repository;

import com.nhnacademy.byeol23backend.cartset.cart.domain.Cart;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CartRepository extends JpaRepository<Cart, Long> {
    Cart findByMember_MemberId(Long memberId);
    boolean existsByMember_MemberId(Long memberId);
}
