package com.nhnacademy.byeol23backend.cartset.cartbook.repository;

import com.nhnacademy.byeol23backend.cartset.cartbook.domain.CartBook;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface CartBookRepository extends JpaRepository<CartBook, Long> {
    CartBook findByCart_CartIdAndBook_BookId(Long cartId, Long bookId);

    @Query("select cb from CartBook cb join fetch cb.book b where cb.cart.cartId = :cartId")
    List<CartBook> findByCartId(@Param("cartId") Long cartId);
}