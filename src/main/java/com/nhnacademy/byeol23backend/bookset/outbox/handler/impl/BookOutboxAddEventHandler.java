package com.nhnacademy.byeol23backend.bookset.outbox.handler.impl;

import com.nhnacademy.byeol23backend.bookset.book.document.BookDocument;
import com.nhnacademy.byeol23backend.bookset.book.document.BookDocumentBuilder;
import com.nhnacademy.byeol23backend.bookset.outbox.BookOutbox;
import com.nhnacademy.byeol23backend.bookset.outbox.handler.BookOutboxEventHandler;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.core.mapping.IndexCoordinates;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class BookOutboxAddEventHandler implements BookOutboxEventHandler {
    private final BookDocumentBuilder bookDocumentBuilder;
    private final ElasticsearchOperations elasticsearchOperations;

    @Override
    public void handle(BookOutbox bookOutbox) {
        BookDocument bookDocument = bookDocumentBuilder.build(bookOutbox.getAggregateId());
        elasticsearchOperations.save(bookDocument, IndexCoordinates.of("byeol23-books"));
        log.info("엘라스틱 서치에 도서 문서 저장: {}", bookDocument.getTitle());
    }

    @Override
    public BookOutbox.EventType getEventType() {
        return BookOutbox.EventType.ADD;
    }
}
