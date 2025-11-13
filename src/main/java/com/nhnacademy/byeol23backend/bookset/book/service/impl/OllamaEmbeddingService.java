package com.nhnacademy.byeol23backend.bookset.book.service.impl;

import com.nhnacademy.byeol23backend.bookset.book.document.BookDocument;
import com.nhnacademy.byeol23backend.bookset.book.service.EmbeddingService;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.ai.embedding.EmbeddingModel;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
public class OllamaEmbeddingService implements EmbeddingService {
    private final EmbeddingModel embeddingModel;

    @Override
    public float[] generateEmbedding(BookDocument bookDocument) {
        String embeddingText = generateEmbeddingText(bookDocument);
        return embeddingModel.embed(embeddingText);
    }

    private String generateEmbeddingText(BookDocument bookDocument) {
        return Stream.of(
                bookDocument.getTitle(),
                bookDocument.getDescription(),
                normalize(bookDocument.getAuthor()),
                normalize(bookDocument.getTagNames()),
                normalize(bookDocument.getPathNames())
        )
                .filter(StringUtils::isNotBlank)
                .collect(Collectors.joining(" "));
    }

    private String normalize(List<String> strings) {
        return strings.stream().filter(StringUtils::isNotBlank).collect(Collectors.joining(" "));
    }
}
