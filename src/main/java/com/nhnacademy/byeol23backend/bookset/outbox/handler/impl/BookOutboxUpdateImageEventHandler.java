package com.nhnacademy.byeol23backend.bookset.outbox.handler.impl;

import com.nhnacademy.byeol23backend.bookset.bookimage.service.BookImageServiceImpl;
import com.nhnacademy.byeol23backend.bookset.outbox.BookOutbox;
import com.nhnacademy.byeol23backend.bookset.outbox.handler.BookOutboxEventHandler;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.core.document.Document;
import org.springframework.data.elasticsearch.core.mapping.IndexCoordinates;
import org.springframework.data.elasticsearch.core.query.UpdateQuery;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class BookOutboxUpdateImageEventHandler implements BookOutboxEventHandler {
    private final ElasticsearchOperations elasticsearchOperations;
    private final BookImageServiceImpl bookImageService;

    @Override
    public void handle(BookOutbox bookOutbox) {
        Long aggregateId = bookOutbox.getAggregateId();
        log.info("도서 문서에 이미지 업데이트");
        String imageUrl = bookImageService.getImageUrlsById(aggregateId).getFirst().getImageUrl();
        log.info("UPDATE_IMAGE 이벤트 수신 image url: {}", imageUrl);
        Document document = Document.create();
        document.put("imageUrl", imageUrl);
        UpdateQuery updateQuery = UpdateQuery.builder(aggregateId.toString())
                .withDocument(document)
                .withDocAsUpsert(true)
                .build();
        elasticsearchOperations.update(updateQuery, IndexCoordinates.of("byeol23-books"));
        log.info("엘라스틱 서치에 도서 이미지 url 저장: {}", imageUrl);
    }

    @Override
    public BookOutbox.EventType getEventType() {
        return BookOutbox.EventType.UPDATE_IMAGE;
    }
}
