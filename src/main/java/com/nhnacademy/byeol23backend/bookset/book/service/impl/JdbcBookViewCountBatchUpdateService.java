package com.nhnacademy.byeol23backend.bookset.book.service.impl;

import com.nhnacademy.byeol23backend.bookset.book.repository.BookRepository;
import com.nhnacademy.byeol23backend.bookset.book.service.BookViewCountBatchUpdateService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class JdbcBookViewCountBatchUpdateService implements BookViewCountBatchUpdateService {
    private final BookRepository bookRepository;

    @Override
    @Transactional
    public void batchUpdate(List<BookViewCountSyncServiceImpl.BookViewCount> bookViewCounts) {
        bookRepository.bookViewCountBatchUpdate(bookViewCounts);
    }
}
