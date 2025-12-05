package com.nhnacademy.byeol23backend.bookset.book.listener;

import com.nhnacademy.byeol23backend.bookset.book.dto.BookReview;
import com.nhnacademy.byeol23backend.bookset.book.event.BookReviewAddEvent;
import com.nhnacademy.byeol23backend.bookset.book.service.BookService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.core.mapping.IndexCoordinates;
import org.springframework.data.elasticsearch.core.query.UpdateQuery;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;
import org.springframework.data.elasticsearch.core.document.Document;

@Slf4j
@Component
@RequiredArgsConstructor
public class BookReviewEventListener {
    private final BookService bookService;
    private final ElasticsearchOperations elasticsearchOperations;

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handleBookReviewAddEvent(BookReviewAddEvent event) {
        BookReview bookReview = bookService.getBookReview(event.bookId());
        int reviewCount = bookReview.reviewCount().intValue();
        float ratingAverage = bookReview.ratingAverage() != null ? bookReview.ratingAverage().floatValue() : 0.0f;
        Document document = Document.create();
        document.put("reviewCount", reviewCount);
        document.put("ratingAverage", ratingAverage);
        UpdateQuery updateQuery = UpdateQuery.builder(event.bookId().toString())
                .withDocument(document)
                .withDocAsUpsert(true)
                .build();
        elasticsearchOperations.update(updateQuery, IndexCoordinates.of("byeol23-books"));
        log.info("도서 문서{} 리뷰, 평점 업데이트", event.bookId());
    }
}
