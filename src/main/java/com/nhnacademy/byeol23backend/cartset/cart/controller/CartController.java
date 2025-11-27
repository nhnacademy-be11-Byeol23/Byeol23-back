package com.nhnacademy.byeol23backend.cartset.cart.controller;

import com.nhnacademy.byeol23backend.cartset.cart.dto.CustomerIdentifier;
import com.nhnacademy.byeol23backend.cartset.cart.service.CartFacadeService;
import com.nhnacademy.byeol23backend.cartset.cartbook.dto.CartBookAddRequest;
import com.nhnacademy.byeol23backend.cartset.cartbook.dto.CartBookResponse;
import com.nhnacademy.byeol23backend.cartset.cartbook.dto.CartBookUpdateRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class CartController {
    private final CartFacadeService cartService;

    @GetMapping("/carts/books")
    public List<CartBookResponse> getCartBooks(CustomerIdentifier identifier) {
        return cartService.getCartBooks(identifier);
    }

    @PostMapping("/carts/books")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void addCartBook(CustomerIdentifier identifier, @RequestBody CartBookAddRequest request) {
        cartService.addBook(identifier,request);
    }

    @PostMapping("/carts/books/{book-id}/update")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void updateCartBook(CustomerIdentifier identifier, @PathVariable("book-id") Long bookId, @RequestBody CartBookUpdateRequest request) {
        cartService.updateCartBook(identifier, bookId, request);
    }

    @PostMapping("/carts/books/{book-id}/delete")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteCartBook(CustomerIdentifier identifier, @PathVariable("book-id") Long bookId) {
        cartService.deleteCartBook(identifier, bookId);
    }
}
