package com.nhnacademy.byeol23backend.bookset.category.service;

import java.util.List;

import com.nhnacademy.byeol23backend.bookset.category.dto.CategoryLeafResponse;
import com.nhnacademy.byeol23backend.bookset.category.dto.CategoryListResponse;
import com.nhnacademy.byeol23backend.bookset.category.dto.CategoryMainPageResponse;
import com.nhnacademy.byeol23backend.bookset.category.dto.CategoryTreeResponse;
import com.nhnacademy.byeol23backend.bookset.category.dto.SubCategoryIdListResponse;

public interface CategoryQueryService {
    List<CategoryListResponse> getRootCategories();
    List<CategoryListResponse> getSubCategories(Long parentId);
    List<CategoryLeafResponse> getLeafCategories();
	List<CategoryMainPageResponse> getLeafCategoriesForMainPage();
    List<CategoryTreeResponse> getCategoriesWithChildren2Depth();
    List<SubCategoryIdListResponse> getSubCategoryIdsByPathId(String pathId);
}
