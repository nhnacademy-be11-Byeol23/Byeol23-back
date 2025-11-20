package com.nhnacademy.byeol23backend.bookset.book.document;

import com.nhnacademy.byeol23backend.bookset.book.document.service.BookDocumentBuilder;
import com.nhnacademy.byeol23backend.bookset.book.exception.BookDocumentEmbeddingBuilderNotFound;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class BookDocumentBuilderRegistry {
    private final List<BookDocumentBuilder> builders;

    public BookDocumentBuilder getBookDocumentEmbeddingBuilder(String eventType) {
        return builders.stream().filter(builder -> builder.getEventType().equals(eventType)).findFirst().orElseThrow(() -> new BookDocumentEmbeddingBuilderNotFound(eventType));
    }
}
