package com.nhnacademy.byeol23backend.cartset.cartbook.service;

import com.nhnacademy.byeol23backend.cartset.cartbook.dto.CartBookResponse;

import java.util.List;
import java.util.Map;

public interface CartBookService {
    List<CartBookResponse> getCartBooks(List<Long> bookIds, Map<Object, Object> cartBookMap);
}
