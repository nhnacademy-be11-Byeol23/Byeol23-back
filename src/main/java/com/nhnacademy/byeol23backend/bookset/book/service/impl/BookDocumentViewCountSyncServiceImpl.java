package com.nhnacademy.byeol23backend.bookset.book.service.impl;

import com.nhnacademy.byeol23backend.bookset.book.document.BookDocument;
import com.nhnacademy.byeol23backend.bookset.book.dto.BookViewCount;
import com.nhnacademy.byeol23backend.bookset.book.repository.BookRepository;
import com.nhnacademy.byeol23backend.bookset.book.service.BookDocumentViewCountSyncService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.core.mapping.IndexCoordinates;
import org.springframework.data.elasticsearch.core.query.UpdateQuery;
import org.springframework.stereotype.Service;
import org.springframework.data.elasticsearch.core.document.Document;

import java.util.List;

@Service
@RequiredArgsConstructor
public class BookDocumentViewCountSyncServiceImpl implements BookDocumentViewCountSyncService {
    private final BookRepository bookRepository;
    private final ElasticsearchOperations elasticsearchOperations;

    @Override
    public void syncBookDocumentViewCount() {
        List<UpdateQuery> updateQueries = bookRepository.findAllBookViewCount().stream()
                .map(bookViewCount -> {
                    Document document = Document.create();
                    document.put("viewCount", bookViewCount.viewCount());
                    return UpdateQuery.builder(bookViewCount.bookId().toString())
                            .withDocument(document)
                            .withDocAsUpsert(false)
                            .build();
                }).toList();

        elasticsearchOperations.bulkUpdate(updateQueries, BookDocument.class);
    }
}
