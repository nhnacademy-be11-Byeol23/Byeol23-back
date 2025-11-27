package com.nhnacademy.byeol23backend.cartset.cartbook.dto;

import com.nhnacademy.byeol23backend.cartset.cartbook.domain.CartBook;

public record CartBookResponse(Long bookId, String imageUrl, String bookName, int quantity, int regularPrice, int salePrice) {
    public static CartBookResponse from(CartBook cartBook) {
        String imageUrl = cartBook.getBook().getBookImageUrls() == null || cartBook.getBook().getBookImageUrls().isEmpty() ? null :
                cartBook.getBook().getBookImageUrls().getFirst().getBookImageUrl();
        return new CartBookResponse(cartBook.getBook().getBookId(), imageUrl,
                cartBook.getBook().getBookName(),
                cartBook.getQuantity(),
                cartBook.getBook().getRegularPrice().intValue(),
                cartBook.getBook().getSalePrice().intValue());
    }
}
