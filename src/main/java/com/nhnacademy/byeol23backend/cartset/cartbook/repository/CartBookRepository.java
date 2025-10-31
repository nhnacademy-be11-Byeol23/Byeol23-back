package com.nhnacademy.byeol23backend.cartset.cartbook.repository;

import com.nhnacademy.byeol23backend.bookset.book.domain.Book;
import com.nhnacademy.byeol23backend.cartset.cart.domain.Cart;
import com.nhnacademy.byeol23backend.cartset.cartbook.domain.CartBook;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CartBookRepository extends JpaRepository<CartBook, Long> {
    Optional<CartBook> findByBookAndCart(Book book, Cart cart);
    void deleteByCartBookId(Long cartBookId);
}