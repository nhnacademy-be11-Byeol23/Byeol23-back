package com.nhnacademy.byeol23backend.bookset.category.service;

import com.nhnacademy.byeol23backend.bookset.category.dto.CategoryLeafResponse;
import com.nhnacademy.byeol23backend.bookset.category.dto.CategoryListResponse;

import java.util.List;

public interface CategoryQueryService {
    List<CategoryListResponse> getRootCategories();
    List<CategoryListResponse> getSubCategories(Long parentId);
    List<CategoryLeafResponse> getLeafCategories();
}
