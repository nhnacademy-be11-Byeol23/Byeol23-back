package com.nhnacademy.byeol23backend.bookset.book.service;

import java.util.List;

import com.nhnacademy.byeol23backend.bookset.book.service.impl.BookViewCountSyncServiceImpl;

public interface BookViewCountBatchUpdateService {
    void batchUpdate(List<BookViewCountSyncServiceImpl.BookViewCount> bookViewCounts);
}
