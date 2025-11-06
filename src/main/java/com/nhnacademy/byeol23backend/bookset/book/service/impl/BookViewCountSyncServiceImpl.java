package com.nhnacademy.byeol23backend.bookset.book.service.impl;

import com.nhnacademy.byeol23backend.bookset.book.service.BookViewCountBatchUpdateService;
import com.nhnacademy.byeol23backend.bookset.book.service.BookViewCountSyncService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.ScanOptions;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class BookViewCountSyncServiceImpl implements BookViewCountSyncService {
    private static final String BOOK_VIEW_COUNT_KEY_PREFIX = "book:viewcount:*";
    private final StringRedisTemplate stringRedisTemplate;
    private final BookViewCountBatchUpdateService bookViewCountBatchUpdateService;

    public record BookViewCount(Long bookId, Long viewCount) {}

    @Override
    public void syncBookViewCount() {
        List<BookViewCount> bookViewCounts = readBookViewCountFromRedis();
        bookViewCountBatchUpdateService.batchUpdate(bookViewCounts);
    }

    private List<BookViewCount> readBookViewCountFromRedis() {
        List<BookViewCount> result = new ArrayList<>();
        ScanOptions scanOptions = ScanOptions.scanOptions().match(BOOK_VIEW_COUNT_KEY_PREFIX).count(100).build();
        stringRedisTemplate.execute((RedisCallback<Void>) connection -> {
            try(var cursor = connection.scan(scanOptions)) {
                while (cursor.hasNext()) {
                    String key = new String(cursor.next(), StandardCharsets.UTF_8);
                    String value = stringRedisTemplate.opsForValue().get(key);
                    if(value == null) continue;
                    result.add(new BookViewCount(extractBookId(key), Long.parseLong(value)));
                    stringRedisTemplate.delete(key);
                }
            }
            return null;
        }, true, false);
        return result;
    }

    private Long extractBookId(String key) {
        return Long.parseLong(key.substring(key.lastIndexOf(":") + 1));
    }
}
