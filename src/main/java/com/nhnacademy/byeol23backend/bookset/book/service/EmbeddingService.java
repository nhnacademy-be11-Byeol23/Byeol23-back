package com.nhnacademy.byeol23backend.bookset.book.service;

import com.nhnacademy.byeol23backend.bookset.book.document.BookDocument;

public interface EmbeddingService {
    float[] generateEmbedding(BookDocument bookDocument);
}
