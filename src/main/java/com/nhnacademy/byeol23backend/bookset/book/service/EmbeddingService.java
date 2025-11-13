package com.nhnacademy.byeol23backend.bookset.book.service;

import com.nhnacademy.byeol23backend.bookset.book.document.BookDocument;

import java.util.List;

public interface EmbeddingService {
    float[] generateEmbedding(BookDocument bookDocument);
}
