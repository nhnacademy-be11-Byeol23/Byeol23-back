package com.nhnacademy.byeol23backend.bookset.book.service;

import com.nhnacademy.byeol23backend.bookset.book.service.impl.BookViewCountSyncServiceImpl;

import java.util.List;

public interface BookViewCountBatchUpdateService {
    void batchUpdate(List<BookViewCountSyncServiceImpl.BookViewCount> bookViewCounts);
}
