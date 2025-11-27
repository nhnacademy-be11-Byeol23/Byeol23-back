package com.nhnacademy.byeol23backend.cartset.cart.service.impl;

import com.nhnacademy.byeol23backend.bookset.book.domain.Book;
import com.nhnacademy.byeol23backend.bookset.book.exception.BookNotFoundException;
import com.nhnacademy.byeol23backend.bookset.book.repository.BookRepository;
import com.nhnacademy.byeol23backend.cartset.cart.domain.Cart;
import com.nhnacademy.byeol23backend.cartset.cart.dto.CustomerIdentifier;
import com.nhnacademy.byeol23backend.cartset.cart.exception.CartNotFoundException;
import com.nhnacademy.byeol23backend.cartset.cart.repository.CartRepository;
import com.nhnacademy.byeol23backend.cartset.cart.service.CartService;
import com.nhnacademy.byeol23backend.cartset.cartbook.domain.CartBook;
import com.nhnacademy.byeol23backend.cartset.cartbook.dto.CartBookAddRequest;
import com.nhnacademy.byeol23backend.cartset.cartbook.dto.CartBookResponse;
import com.nhnacademy.byeol23backend.cartset.cartbook.dto.CartBookUpdateRequest;
import com.nhnacademy.byeol23backend.cartset.cartbook.repository.CartBookRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class MemberCartService implements CartService {
    private final CartRepository cartRepository;
    private final CartBookRepository cartBookRepository;
    private final BookRepository bookRepository;

    @Override
    @Transactional
    public void addBook(CustomerIdentifier identifier, CartBookAddRequest request) {
        Cart cart = cartRepository.findByMember_MemberId(identifier.memberId());
        Book book = bookRepository.findById(request.bookId()).orElseThrow(() -> new BookNotFoundException("존재하지 않는 도서입니다."));
        CartBook findCartBook = cartBookRepository.findByCart_CartIdAndBook_BookId(cart.getCartId(), request.bookId());

        if(findCartBook != null) {
            findCartBook.increaseQuantity(request.quantity());
            return;
        }

        CartBook cartBook = new CartBook(request.quantity(), book, cart);
        cartBookRepository.save(cartBook);
        log.info("장바구니 {}번 도서 {}개 추가", request.bookId(), request.quantity());
    }

    @Override
    @Transactional(readOnly = true)
    public List<CartBookResponse> getCartBooks(CustomerIdentifier identifier) {
        Cart cart = cartRepository.findByMember_MemberId(identifier.memberId());
        return cartBookRepository.findByCartId(cart.getCartId()).stream()
                .map(CartBookResponse::from)
                .toList();
    }

    @Override
    @Transactional
    public void updateQuantity(CustomerIdentifier identifier, Long bookId, CartBookUpdateRequest request) {
        Cart cart = cartRepository.findByMember_MemberId(identifier.memberId());
        CartBook findCartBook = cartBookRepository.findByCart_CartIdAndBook_BookId(cart.getCartId(), bookId);
        findCartBook.updateQuantity(request.quantity());
        log.info("장바구니 {}번 도서 {}개로 변경", bookId, request.quantity());
    }

    @Override
    @Transactional
    public void deleteBook(CustomerIdentifier identifier, Long bookId) {
        cartBookRepository.deleteByBook_BookId(bookId);
    }
}
