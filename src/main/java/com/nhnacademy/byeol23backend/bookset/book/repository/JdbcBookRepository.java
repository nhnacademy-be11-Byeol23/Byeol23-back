package com.nhnacademy.byeol23backend.bookset.book.repository;

import com.nhnacademy.byeol23backend.bookset.book.service.impl.BookViewCountSyncServiceImpl;

import java.util.List;

public interface JdbcBookRepository {
    void bookViewCountBatchUpdate(List<BookViewCountSyncServiceImpl.BookViewCount> bookViewCounts);
}
