package com.nhnacademy.byeol23backend.bookset.book.service.impl;

import com.nhnacademy.byeol23backend.bookset.book.service.BookViewCountService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class RedisBookViewCountService implements BookViewCountService {
    private final StringRedisTemplate stringRedisTemplate;

    @Override
    public void increaseViewCount(Long bookId, String viewerId) {
        Boolean isFirstViewed = stringRedisTemplate.opsForValue().setIfAbsent(generateBookViewedKey(bookId, viewerId), "1", Duration.ofHours(1));
        if(Boolean.TRUE.equals(isFirstViewed)) {
            Long increment = stringRedisTemplate.opsForValue().increment(generateBookViewCountKey(bookId));
            log.info("도서 번호:{} 조회수 {}", bookId, increment);
        }
    }

    @Override
    public long getViewCount(Long bookId) {
        return Optional.ofNullable(stringRedisTemplate.opsForValue().get(generateBookViewCountKey(bookId)))
                .map(Long::parseLong)
                .orElse(0L);
    }

    private String generateBookViewedKey(Long bookId, String viewerId) {
        return String.format("book:viewed:%d:%s", bookId, viewerId);
    }
    private String generateBookViewCountKey(Long bookId) {
        return String.format("book:viewcount:%d", bookId);
    }
}
