package com.nhnacademy.byeol23backend.bookset.category.service.impl;

import com.nhnacademy.byeol23backend.bookset.category.dto.CategoryListResponse;
import com.nhnacademy.byeol23backend.bookset.category.repository.CategoryRepository;
import com.nhnacademy.byeol23backend.bookset.category.service.CategoryQueryService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

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
}
