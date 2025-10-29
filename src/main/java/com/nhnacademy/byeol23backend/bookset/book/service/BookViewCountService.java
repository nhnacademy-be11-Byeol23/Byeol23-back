package com.nhnacademy.byeol23backend.bookset.book.service;

public interface BookViewCountService {
    void increaseViewCount(Long bookId, String viewerId);
}
