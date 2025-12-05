package com.nhnacademy.byeol23backend.couponset.coupon.service;

import com.nhnacademy.byeol23backend.bookset.book.domain.Book;
import com.nhnacademy.byeol23backend.bookset.book.repository.BookRepository;
import com.nhnacademy.byeol23backend.couponset.coupon.dto.OrderContext;
import com.nhnacademy.byeol23backend.couponset.coupon.dto.OrderItemRequest;
import com.nhnacademy.byeol23backend.couponset.couponpolicy.domain.CouponPolicy;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.List;

@Component
@RequiredArgsConstructor
public class BookCouponCalculationStrategy implements CouponCalculationStrategy {

    private final BookRepository bookRepository;

    @Override
    public BigDecimal calculateTargetSubtotal(CouponPolicy policy, List<OrderItemRequest> items, OrderContext context) {
        // BOOK 정책: 정책에 포함된 도서만 필터링하여 합산
        return items.stream()
                .filter(item -> bookRepository.isBookIncludedInPolicy(item.bookId(), policy.getCouponPolicyId()))
                .map(item -> {
                    Book book = bookRepository.findByBookId(item.bookId()).orElseThrow();
                    return book.getSalePrice().multiply(BigDecimal.valueOf(item.quantity()));
                })
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }
}