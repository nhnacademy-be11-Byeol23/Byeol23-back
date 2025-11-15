package com.nhnacademy.byeol23backend.bookset.book.listener;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.nhnacademy.byeol23backend.bookset.book.document.BookDocument;
import com.nhnacademy.byeol23backend.bookset.book.domain.Book;
import com.nhnacademy.byeol23backend.bookset.book.event.BookDocumentAddEvent;
import com.nhnacademy.byeol23backend.bookset.book.event.BookDocumentDeleteEvent;
import com.nhnacademy.byeol23backend.bookset.book.event.BookDocumentUpdateEvent;
import com.nhnacademy.byeol23backend.bookset.book.service.BookService;
import com.nhnacademy.byeol23backend.bookset.bookcategory.service.BookCategoryService;
import com.nhnacademy.byeol23backend.bookset.bookcontributor.service.BookContributorService;
import com.nhnacademy.byeol23backend.bookset.booktag.service.BookTagService;
import com.nhnacademy.byeol23backend.bookset.category.domain.Category;
import com.nhnacademy.byeol23backend.bookset.contributor.domain.Contributor;
import com.nhnacademy.byeol23backend.bookset.tag.domain.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Component
@RequiredArgsConstructor
public class BookDocumentEventListener {
    private final ElasticsearchOperations elasticsearchOperations;
    private final StringRedisTemplate stringRedisTemplate;
    private final ObjectMapper objectMapper;
    private final BookService bookService;
    private final BookCategoryService bookCategoryService;
    private final BookContributorService bookContributorService;
    private final BookTagService bookTagService;

    private static final String FULL_REINDEX_KEY = "batch:full-reindex:lock";
    private static final String BOOK_DOCUMENT_ADD_KEY = "book-document:add:queue";
    private static final String BOOK_DOCUMENT_UPDATE_KEY = "book-document:update:queue";
    private static final String BOOK_DOCUMENT_DELETE_KEY = "book-document:delete:queue";

    @Async("ioExecutor")
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handleBookDocumentAddEvent(BookDocumentAddEvent event) {
        BookDocument bookDocument = build(event.bookId());
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

    private BookDocument build(Long bookId) {
        Book book = bookService.getBookWithPublisher(bookId);
        List<Category> categories = bookCategoryService.getCategoriesByBookId(bookId);
        Map<String, List<Contributor>> contributorMap = bookContributorService.getContributorsByBookId(bookId).stream().collect(Collectors.groupingBy(Contributor::getContributorRole));
        List<Tag> tags = bookTagService.getTagsByBookId(bookId);

        return BookDocument.builder()
                .id(String.valueOf(book.getBookId()))
                .title(book.getBookName())
                .author(contributorMap.get("저자").stream().map(Contributor::getContributorName).toList())
                .translator(contributorMap.getOrDefault("역자", List.of()).stream().map(Contributor::getContributorName).toList())
                .isbn(book.getIsbn())
                .regularPrice(book.getRegularPrice().intValue())
                .salePrice(book.getSalePrice().intValue())
                .publisher(book.getPublisher().getPublisherName())
                .publishedAt(book.getPublishDate())
                .tagNames(tags.stream().map(Tag::getTagName).toList())
                .pathIds(categories.stream().map(Category::getPathId).toList())
                .pathNames(categories.stream().map(Category::getPathName).toList())
                .viewCount(book.getViewCount())
                .reviewCount(0)
                .ratingAverage(0.0f)
                .bookStatus(book.getBookStatus())
                .build();
    }
}
