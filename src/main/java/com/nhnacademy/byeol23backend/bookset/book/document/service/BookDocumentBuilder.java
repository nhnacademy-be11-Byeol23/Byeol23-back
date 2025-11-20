package com.nhnacademy.byeol23backend.bookset.book.document.service;

import com.nhnacademy.byeol23backend.bookset.book.document.BookDocument;

public interface BookDocumentBuilder {
    BookDocument build(Long bookId);

    String getEventType();
}
