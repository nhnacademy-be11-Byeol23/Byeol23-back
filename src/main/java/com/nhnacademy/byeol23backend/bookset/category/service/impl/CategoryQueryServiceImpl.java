package com.nhnacademy.byeol23backend.bookset.category.service.impl;

import com.nhnacademy.byeol23backend.bookset.category.dto.*;
import com.nhnacademy.byeol23backend.bookset.category.repository.CategoryRepository;
import com.nhnacademy.byeol23backend.bookset.category.service.CategoryQueryService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CategoryQueryServiceImpl implements CategoryQueryService {
    private final CategoryRepository categoryRepository;

    @Override
    public List<CategoryListResponse> getRootCategories() {
        return categoryRepository.findRootCategories();
    }

    @Override
    public List<CategoryListResponse> getSubCategories(Long parentId) {
        return categoryRepository.findChildrenCategories(parentId);
    }

    @Override
    public List<CategoryLeafResponse> getLeafCategories() {
        return categoryRepository.findLeafCategories();
    }

    @Override
    @Transactional(readOnly = true)
    public List<CategoryTreeResponse> getCategoriesWithChildren2Depth() {
        return categoryRepository.findRootCategoryEntities().stream()
                .map(root -> CategoryTreeResponse.from(root, 2)).toList();
    }

    @Override
    public List<SubCategoryIdListResponse> getSubCategoryIdsByPathId(String pathId) {
        return categoryRepository.findSubCategoryIdsByPathId(pathId);
    }
}
