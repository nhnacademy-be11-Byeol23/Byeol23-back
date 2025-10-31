package com.nhnacademy.byeol23backend.cartset.cartbook.service.impl;

import com.nhnacademy.byeol23backend.bookset.book.domain.Book;
import com.nhnacademy.byeol23backend.bookset.book.exception.BookNotFoundException;
import com.nhnacademy.byeol23backend.bookset.book.repository.BookRepository;
import com.nhnacademy.byeol23backend.cartset.cart.domain.Cart;
import com.nhnacademy.byeol23backend.cartset.cart.exception.CartNotFoundException;
import com.nhnacademy.byeol23backend.cartset.cart.repository.CartRepository;
import com.nhnacademy.byeol23backend.cartset.cartbook.domain.CartBook;
import com.nhnacademy.byeol23backend.cartset.cartbook.domain.dto.CartBookAddRequest;
import com.nhnacademy.byeol23backend.cartset.cartbook.domain.dto.CartBookUpdateRequest;
import com.nhnacademy.byeol23backend.cartset.cartbook.repository.CartBookRepository;
import com.nhnacademy.byeol23backend.cartset.cartbook.service.CartBookService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional
public class CartBookServiceImpl implements CartBookService {

    private final CartBookRepository cartBookRepository;
    private final BookRepository bookRepository;
    private final CartRepository cartRepository;

    @Override
    public void addCartItem(CartBookAddRequest cartBookAddRequest) {
        Book book = bookRepository.findById(cartBookAddRequest.bookId())
                .orElseThrow(() -> new BookNotFoundException("해당 도서를 찾을 수 없습니다.: " + cartBookAddRequest.bookId()));

        Cart cart = cartRepository.findById(cartBookAddRequest.cartId())
                .orElseThrow(() -> new CartNotFoundException("해당 장바구니를 찾을 수 없습니다.: " + cartBookAddRequest.cartId()));

        Optional<CartBook> optionalCartBook = cartBookRepository.findByBookAndCart(book, cart);

        if (optionalCartBook.isPresent()) {
            // 장바구니에 동일 도서가 이미 있으면 수량 증가 후 저장
            CartBook cartBook = optionalCartBook.get();
            cartBook.setQuantity(cartBook.getQuantity() + cartBookAddRequest.quantity());
            cartBookRepository.save(cartBook);
        } else {
            // 장바구니에 동일 도서가 없으면 새로 생성 후 저장
            CartBook cartBook = new CartBook();
            cartBook.setCart(cart);
            cartBook.setBook(book);
            cartBook.setQuantity(cartBookAddRequest.quantity());
            cartBookRepository.save(cartBook);
        }
    }

    @Override
    public void deleteCartItem(Long cartBookId) {
        cartBookRepository.deleteByCartBookId(cartBookId);
    }

    @Override
    public void updateCartItemQuantity(CartBookUpdateRequest cartBookUpdateRequest) {
        CartBook cartBook = cartBookRepository.findById(cartBookUpdateRequest.cartBookId())
                .orElseThrow(() -> new BookNotFoundException("해당 도서를 찾을 수 없습니다.: " + cartBookUpdateRequest.cartBookId()));

        cartBook.setQuantity(cartBookUpdateRequest.quantity());
        cartBookRepository.save(cartBook);
    }
}
