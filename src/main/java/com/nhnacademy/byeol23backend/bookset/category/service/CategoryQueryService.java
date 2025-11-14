package com.nhnacademy.byeol23backend.bookset.category.service;

import com.nhnacademy.byeol23backend.bookset.category.dto.*;

import java.util.List;

public interface CategoryQueryService {
    List<CategoryListResponse> getRootCategories();
    List<CategoryListResponse> getSubCategories(Long parentId);
    List<CategoryLeafResponse> getLeafCategories();
    List<CategoryTreeResponse> getCategoriesWithChildren2Depth();
    List<SubCategoryIdListResponse> getSubCategoryIdsByPathId(String pathId);
}
