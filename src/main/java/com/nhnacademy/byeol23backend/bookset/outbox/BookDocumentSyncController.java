package com.nhnacademy.byeol23backend.bookset.outbox;

import com.nhnacademy.byeol23backend.bookset.outbox.service.BookOutboxService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/books")
@RequiredArgsConstructor
public class BookDocumentSyncController {
    private final BookOutboxService bookOutboxService;

    @PostMapping("/{book-id}/publish")
    public void publishBookOutbox(@PathVariable("book-id") Long bookId, @RequestBody BookOutbox.EventType eventType) {
        bookOutboxService.publishBookOutbox(bookId, eventType);
    }
}
