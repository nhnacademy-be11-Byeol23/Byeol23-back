package com.nhnacademy.byeol23backend.bookset.book.exception;

public class BookDocumentEmbeddingBuilderNotFound extends RuntimeException {
    public BookDocumentEmbeddingBuilderNotFound(String eventType) {
        super("%s에 맞는 BookDocumentEmbeddingBuilder 인스턴스를 찾을 수 없습니다.".formatted(eventType));
    }
}
