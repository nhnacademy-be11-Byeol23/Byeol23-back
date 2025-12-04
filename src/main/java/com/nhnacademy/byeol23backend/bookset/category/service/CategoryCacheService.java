package com.nhnacademy.byeol23backend.bookset.category.service;

import java.util.List;

import com.nhnacademy.byeol23backend.bookset.category.dto.CategoryTreeResponse;

public interface CategoryCacheService {
    void cacheCategoryTree2Depth();
    List<CategoryTreeResponse> getRootsWithChildren2Depth();
}
