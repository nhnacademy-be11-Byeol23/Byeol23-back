package com.nhnacademy.byeol23backend.bookset.category.service.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
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
        updateCategoryTree2Depth(categories);
    }

    @Override
    public List<CategoryTreeResponse> getRootsWithChildren2Depth() {
        String categoryTreeResponseJson = stringRedisTemplate.opsForValue().get(CATEGORY_TREE_KEY);
        if(categoryTreeResponseJson == null) {
            log.info("루트 카테고리 2계층 트리  캐시 미스 -> DB 조회 후 캐시 저장");
            List<CategoryTreeResponse> categories = categoryQueryService.getCategoriesWithChildren2Depth();
            updateCategoryTree2Depth(categories);
            return categories;
        }
        try {
            return objectMapper.readValue(categoryTreeResponseJson, new TypeReference<>() {});
        } catch (JsonProcessingException e) {
            log.error("루트 카테고리 2계층 트리 JSON 역직렬화 실패 -> DB 조회 후 캐시 저장");
            List<CategoryTreeResponse> categories = categoryQueryService.getCategoriesWithChildren2Depth();
            updateCategoryTree2Depth(categories);
            return categories;
        }
    }

    private void updateCategoryTree2Depth(List<CategoryTreeResponse> categories) {
        try {
            String categoryTreeResponseJson = objectMapper.writeValueAsString(categories);
            stringRedisTemplate.opsForValue().set(CATEGORY_TREE_KEY, categoryTreeResponseJson);
            log.info("루트 카테고리 2계층 트리 캐시 저장: {}", categoryTreeResponseJson);
        } catch (JsonProcessingException ignored) {
            log.error("루트 카테고리 2계층 트리 JSON 직렬화 실패");
        }
    }
}
