package com.nhnacademy.byeol23backend.cartset.cartbook.service.impl;

import com.nhnacademy.byeol23backend.bookset.book.domain.Book;
import com.nhnacademy.byeol23backend.bookset.book.repository.BookRepository;
import com.nhnacademy.byeol23backend.bookset.bookimage.domain.BookImage;
import com.nhnacademy.byeol23backend.cartset.cartbook.dto.CartBookResponse;
import com.nhnacademy.byeol23backend.cartset.cartbook.service.CartBookService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class GuestCartBookService implements CartBookService {
    private final BookRepository bookRepository;

    @Override
    @Transactional(readOnly = true)
    public List<CartBookResponse> getCartBooks(List<Long> bookIds, Map<Object, Object> cartBookMap) {
        Map<Long, Book> bookMap = bookRepository.findByBookIdIn(bookIds).stream().collect(Collectors.toMap(Book::getBookId, Function.identity()));

        return bookIds.stream()
                .map(id -> {
                    Book book = bookMap.get(id);
                    int qty = Integer.parseInt((String) cartBookMap.get(String.valueOf(id)));
                    String imageUrl = book.getBookImageUrls() == null || book.getBookImageUrls().isEmpty() ? null : book.getBookImageUrls().getFirst().getBookImageUrl();
                    return new CartBookResponse(id, imageUrl, book.getBookName(), qty, book.getRegularPrice().intValue(), book.getSalePrice().intValue());
                })
                .toList();
    }
}
