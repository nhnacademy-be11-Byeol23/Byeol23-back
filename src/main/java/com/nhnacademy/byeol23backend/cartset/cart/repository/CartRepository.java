package com.nhnacademy.byeol23backend.cartset.cart.repository;

import com.nhnacademy.byeol23backend.cartset.cart.domain.Cart;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface CartRepository extends JpaRepository<Cart, Long> {
    void deleteByMember_MemberId(Long memberId);

    // 회원 ID로 장바구니를 조회하면서 Cart 안의 CartBook과 Book 정보를 함께 가져옴
    @Query("""
        SELECT DISTINCT c
        FROM Cart c
        LEFT JOIN FETCH c.cartBooks cb
        LEFT JOIN FETCH cb.book b
        WHERE c.member.memberId = :memberId
    """)
    Optional<Cart> findCartWithBooksByMemberId(@Param("memberId") Long memberId);
}
