package com.nhnacademy.byeol23backend.bookset.book.dto;

import java.util.List;

public record BookOrderRequest(List<BookInfoRequest> bookList) {
}