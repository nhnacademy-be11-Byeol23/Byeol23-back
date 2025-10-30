package com.nhnacademy.byeol23backend.bookset.book.listener;

import com.nhnacademy.byeol23backend.bookset.book.event.ViewCountIncreaseEvent;
import com.nhnacademy.byeol23backend.bookset.book.service.BookViewCountService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ViewCountEventListener {
    private final BookViewCountService bookViewCountService;

    @Async("ioExecutor")
    @EventListener
    public void handleViewCountEvent(ViewCountIncreaseEvent event) {
        bookViewCountService.increaseViewCount(event.bookId(), event.viewerId());
    }
}
