package com.nhnacademy.byeol23backend.bookset.category.listener;

import com.nhnacademy.byeol23backend.bookset.category.event.CategoryTreeCacheUpdateEvent;
import com.nhnacademy.byeol23backend.bookset.category.service.CategoryCacheService;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Component
@RequiredArgsConstructor
public class CategoryTreeCacheEventListener {
    private final CategoryCacheService categoryCacheService;

    @Async("ioExecutor")
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handleCategoryTreeCacheUpdateEvent(CategoryTreeCacheUpdateEvent event) {
        categoryCacheService.cacheCategoryTree2Depth();
    }
}
