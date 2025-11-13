package com.nhnacademy.byeol23backend.bookset.category.service;

import com.nhnacademy.byeol23backend.bookset.category.dto.CategoryTreeResponse;

import java.util.List;

public interface CategoryCacheService {
    void cacheCategoryTree2Depth();
    List<CategoryTreeResponse> getRootsWithChildren2Depth();
}
