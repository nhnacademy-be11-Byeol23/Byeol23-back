package com.nhnacademy.byeol23backend.bookset.outbox.consumer;

import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.core.mapping.IndexCoordinates;
import org.springframework.stereotype.Component;

import com.nhnacademy.byeol23backend.bookset.book.document.BookDocument;
import com.nhnacademy.byeol23backend.bookset.book.document.BookDocumentBuilderRegistry;
import com.nhnacademy.byeol23backend.bookset.book.service.EmbeddingService;
import com.nhnacademy.byeol23backend.bookset.outbox.dto.BookDocumentSyncMessage;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
public class BookEmbeddingConsumer {
    private final EmbeddingService embeddingService;
    private final ElasticsearchOperations elasticsearchOperations;
    private final BookDocumentBuilderRegistry bookDocumentBuilderRegistry;

    @RabbitListener(queues = "${book.embedding.queue}", autoStartup = "true")
    public void consume(BookDocumentSyncMessage message) {
        BookDocument bookDocument = bookDocumentBuilderRegistry.getBookDocumentEmbeddingBuilder(message.getEventType()).build(message.getBookId());
        log.info("임베딩 하지 않은 도서 문서 빌드: {}", bookDocument.getTitle());

        float[] embed = embeddingService.generateEmbedding(bookDocument);
        bookDocument.setEmbedding(embed);
        log.info("도서 문서에 임베딩 설정");

        elasticsearchOperations.save(bookDocument, IndexCoordinates.of("byeol23-books"));
        log.info("엘라스틱서치에 {} 문서 저장 완료", bookDocument.getTitle());
    }
}
