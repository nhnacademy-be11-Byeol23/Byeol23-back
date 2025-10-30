package com.nhnacademy.byeol23backend.bookset.book.event;

import lombok.AllArgsConstructor;
import lombok.Getter;

public record ViewCountIncreaseEvent(Long bookId, String viewerId) {
}
