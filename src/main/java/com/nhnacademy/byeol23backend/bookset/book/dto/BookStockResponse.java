package com.nhnacademy.byeol23backend.bookset.book.dto;

public record BookStockResponse(
	Long bookId,
	String bookName,
	Integer stock
) {
}
