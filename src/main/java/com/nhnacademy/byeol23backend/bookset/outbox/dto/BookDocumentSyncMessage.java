package com.nhnacademy.byeol23backend.bookset.outbox.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class BookDocumentSyncMessage {
    private Long bookId;
    private String eventType;
}
