package com.nhnacademy.byeol23backend.bookset.book.exception;

public class BookStockNotEnoughException extends RuntimeException {
	public BookStockNotEnoughException(String message) {
		super(message);
	}
}
