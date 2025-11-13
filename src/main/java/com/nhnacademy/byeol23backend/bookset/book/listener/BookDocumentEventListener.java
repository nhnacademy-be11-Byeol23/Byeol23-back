package com.nhnacademy.byeol23backend.bookset.book.listener;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.nhnacademy.byeol23backend.bookset.book.document.BookDocument;
import com.nhnacademy.byeol23backend.bookset.book.event.BookDocumentAddEvent;
import com.nhnacademy.byeol23backend.bookset.book.event.BookDocumentDeleteEvent;
import com.nhnacademy.byeol23backend.bookset.book.event.BookDocumentUpdateEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Slf4j
@Component
@RequiredArgsConstructor
public class BookDocumentEventListener {
    private final ElasticsearchOperations elasticsearchOperations;
    private final StringRedisTemplate stringRedisTemplate;
    private final ObjectMapper objectMapper;
    private static final String FULL_REINDEX_KEY = "batch:full-reindex:lock";
    private static final String BOOK_DOCUMENT_ADD_KEY = "book-document:add:queue";
    private static final String BOOK_DOCUMENT_UPDATE_KEY = "book-document:update:queue";
    private static final String BOOK_DOCUMENT_DELETE_KEY = "book-document:delete:queue";

    @Async("ioExecutor")
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handleBookDocumentAddEvent(BookDocumentAddEvent event) {
        BookDocument bookDocument = event.bookDocument();
        if(Boolean.TRUE.equals(stringRedisTemplate.hasKey(FULL_REINDEX_KEY))) {
            try {
                String bookDocumentJson = objectMapper.writeValueAsString(bookDocument);
                stringRedisTemplate.opsForList().rightPush(BOOK_DOCUMENT_ADD_KEY, bookDocumentJson);
                log.info("배치 서버에서 전체 색인 중... 레디스에 도서 문서 큐잉: {}", bookDocument.getTitle());
            } catch (JsonProcessingException e) {
                log.warn("도서 문서 {} JSON 직렬화 실패: {}", bookDocument.getId(), e.getMessage());
            }
        }
        else {
            elasticsearchOperations.save(bookDocument);
            log.info("엘라스틱서치에 도서 문서 저장: {}", bookDocument.getTitle());
        }
    }

    @Async("ioExecutor")
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handleBookDocumentUpdateEvent(BookDocumentUpdateEvent event) {

    }

    @Async("ioExecutor")
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handleBookDocumentDeleteEvent(BookDocumentDeleteEvent event) {
        if(Boolean.TRUE.equals(stringRedisTemplate.hasKey(FULL_REINDEX_KEY))) {
            stringRedisTemplate.opsForList().rightPush(BOOK_DOCUMENT_DELETE_KEY, event.id());
            log.info("배치 서버에서 전체 색인 중... 레디스에 도서 문서 번호 큐잉: {}", event.id());
        }
        elasticsearchOperations.delete(event.id(), BookDocument.class);
        log.info("엘라스틱서치에 도서 문서 삭제: {}", event.id());
    }
}
