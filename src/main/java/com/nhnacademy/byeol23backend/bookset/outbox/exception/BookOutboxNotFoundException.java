package com.nhnacademy.byeol23backend.bookset.outbox.exception;

public class BookOutboxNotFoundException extends RuntimeException {
    public BookOutboxNotFoundException(String message) {
        super(message);
    }
}
