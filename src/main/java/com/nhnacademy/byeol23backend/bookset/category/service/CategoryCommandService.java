package com.nhnacademy.byeol23backend.bookset.category.service;

import com.nhnacademy.byeol23backend.bookset.category.dto.CategoryCreateRequest;
import com.nhnacademy.byeol23backend.bookset.category.dto.CategoryUpdateRequest;
import com.nhnacademy.byeol23backend.bookset.category.dto.CategoryUpdateResponse;

public interface CategoryCommandService {
    void createCategory(CategoryCreateRequest addRequest);
    CategoryUpdateResponse updateCategory(Long id, CategoryUpdateRequest updateRequest);
    void deleteCategory(Long id);
}
