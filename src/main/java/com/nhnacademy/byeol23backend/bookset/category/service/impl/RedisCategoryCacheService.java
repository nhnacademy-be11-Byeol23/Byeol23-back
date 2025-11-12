package com.nhnacademy.byeol23backend.bookset.category.service.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.nhnacademy.byeol23backend.bookset.category.dto.CategoryTreeResponse;
import com.nhnacademy.byeol23backend.bookset.category.service.CategoryCacheService;
import com.nhnacademy.byeol23backend.bookset.category.service.CategoryQueryService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class RedisCategoryCacheService implements CategoryCacheService {
    private static final String CATEGORY_TREE_KEY = "category:tree:2depth";
    private final StringRedisTemplate stringRedisTemplate;
    private final CategoryQueryService categoryQueryService;
    private final ObjectMapper objectMapper;

    @Override
    public void cacheCategoryTree2Depth() {
        List<CategoryTreeResponse> categories = categoryQueryService.getCategoriesWithChildren2Depth();
        try {
            String jsonString = objectMapper.writeValueAsString(categories);
            stringRedisTemplate.opsForValue().set(CATEGORY_TREE_KEY, jsonString);
            log.info("루트 카테고르 2계층 트리 캐시 저장: {}", jsonString);
        } catch (JsonProcessingException ignored) {
            log.info("루트 카테고리 2계층 트리 JSON 직렬화 실패");
        }
    }

}
