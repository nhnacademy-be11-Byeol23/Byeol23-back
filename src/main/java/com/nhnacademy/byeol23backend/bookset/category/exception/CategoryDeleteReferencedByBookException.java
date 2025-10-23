package com.nhnacademy.byeol23backend.bookset.category.exception;

public class CategoryDeleteReferencedByBookException extends RuntimeException {
    public CategoryDeleteReferencedByBookException(String message) {
        super(message);
    }
}
