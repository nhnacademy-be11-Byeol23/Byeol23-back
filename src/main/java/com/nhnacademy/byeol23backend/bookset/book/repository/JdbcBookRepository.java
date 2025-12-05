package com.nhnacademy.byeol23backend.bookset.book.repository;

import java.util.List;

import com.nhnacademy.byeol23backend.bookset.book.service.impl.BookViewCountSyncServiceImpl;

public interface JdbcBookRepository {
    void bookViewCountBatchUpdate(List<BookViewCountSyncServiceImpl.BookViewCount> bookViewCounts);
}
