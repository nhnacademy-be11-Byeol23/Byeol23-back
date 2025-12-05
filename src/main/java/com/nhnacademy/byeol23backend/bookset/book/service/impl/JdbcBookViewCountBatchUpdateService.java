package com.nhnacademy.byeol23backend.bookset.book.service.impl;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.nhnacademy.byeol23backend.bookset.book.repository.BookRepository;
import com.nhnacademy.byeol23backend.bookset.book.service.BookViewCountBatchUpdateService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class JdbcBookViewCountBatchUpdateService implements BookViewCountBatchUpdateService {
    private final BookRepository bookRepository;

    @Override
    @Transactional
    public void batchUpdate(List<BookViewCountSyncServiceImpl.BookViewCount> bookViewCounts) {
        bookRepository.bookViewCountBatchUpdate(bookViewCounts);
        log.info("도서 상세 페이지 조회수 업데이트: {}", bookViewCounts.size());
    }
}
