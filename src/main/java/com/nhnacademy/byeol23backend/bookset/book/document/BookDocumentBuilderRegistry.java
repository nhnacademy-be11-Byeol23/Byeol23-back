package com.nhnacademy.byeol23backend.bookset.book.document;

import java.util.List;

import org.springframework.stereotype.Component;

import com.nhnacademy.byeol23backend.bookset.book.document.service.BookDocumentBuilder;
import com.nhnacademy.byeol23backend.bookset.book.exception.BookDocumentEmbeddingBuilderNotFound;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class BookDocumentBuilderRegistry {
    private final List<BookDocumentBuilder> builders;

    public BookDocumentBuilder getBookDocumentEmbeddingBuilder(String eventType) {
        return builders.stream().filter(builder -> builder.getEventType().equals(eventType)).findFirst().orElseThrow(() -> new BookDocumentEmbeddingBuilderNotFound(eventType));
    }
}
