package com.nhnacademy.byeol23backend.bookset.category.service.impl;

import com.nhnacademy.byeol23backend.bookset.bookcategory.repository.BookCategoryRepository;
import com.nhnacademy.byeol23backend.bookset.category.domain.Category;
import com.nhnacademy.byeol23backend.bookset.category.dto.CategoryCreateRequest;
import com.nhnacademy.byeol23backend.bookset.category.dto.CategoryUpdateRequest;
import com.nhnacademy.byeol23backend.bookset.category.dto.CategoryUpdateResponse;
import com.nhnacademy.byeol23backend.bookset.category.exception.CategoryDeleteReferencedByBookException;
import com.nhnacademy.byeol23backend.bookset.category.exception.CategoryNotFoundException;
import com.nhnacademy.byeol23backend.bookset.category.repository.CategoryRepository;
import com.nhnacademy.byeol23backend.bookset.category.service.CategoryCommandService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class CategoryCommandServiceImpl implements CategoryCommandService {
    private final CategoryRepository categoryRepository;
    private final BookCategoryRepository bookCategoryRepository;

    @Override
    @Transactional
    public void createCategory(CategoryCreateRequest addRequest) {
        String saveCategoryName = addRequest.name();
        Long parentId = addRequest.parentId();

        Category parent = null;

        if(parentId != null) {
            parent = categoryRepository.findById(parentId)
                    .orElseThrow(() -> new CategoryNotFoundException("부모 카테고리를 찾을 수 없습니다."));
        }

        Category saveCategory = new Category(saveCategoryName, parent);
        Category savedCategory = categoryRepository.save(saveCategory);
        setCategoryPath(parent, savedCategory);
        log.info("카테고리 생성:{}", savedCategory.getCategoryName());
    }

    @Override
    @Transactional
    public CategoryUpdateResponse updateCategory(Long id, CategoryUpdateRequest updateRequest) {
        Category updateCategory = categoryRepository.findById(id).orElseThrow(() -> new CategoryNotFoundException("카테고리를 찾을 수 없습니다."));
        Category parent = updateCategory.getParent();
        String updateCategoryName = updateRequest.name();

        updateCategory.updateName(updateCategoryName);

        String newPathName = (parent == null) ? updateCategory.getCategoryName() : parent.getPathName() + "/" + updateCategory.getCategoryName();
        String oldPathName = updateCategory.getPathName();
        String currentPathId = updateCategory.getPathId();

        updateCategory.updatePath(currentPathId, newPathName);
        categoryRepository.updateSubPathNames(currentPathId, oldPathName, newPathName);
        log.info("카테고리 수정:{}", updateCategoryName);
        return CategoryUpdateResponse.from(updateCategory);
    }

    @Override
    @Transactional
    public void deleteCategory(Long id) {
        Category deleteCategory = categoryRepository.findById(id).orElseThrow(() -> new CategoryNotFoundException("카테고리를 찾을 수 없습니다."));
        if(bookCategoryRepository.existsByCategoryPathIdLike(deleteCategory.getPathId())) {
            throw new CategoryDeleteReferencedByBookException("카테고리를 참조하는 도서가 존재합니다.");
        }
        categoryRepository.deleteById(id);
        log.info("카테고리 삭제:{}", id);
    }

    private void setCategoryPath(Category parent, Category child) {
        if(parent == null) child.updatePath(String.valueOf(child.getCategoryId()), child.getCategoryName());
        else child.updatePath(parent.getPathId() + "/" + child.getCategoryId(), parent.getPathName() + "/" + child.getCategoryName());
    }
}