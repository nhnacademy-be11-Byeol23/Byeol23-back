package com.nhnacademy.byeol23backend.bookset.book.event;

public record ViewCountIncreaseEvent(Long bookId, String viewerId) {
}
