package com.nhnacademy.byeol23backend.bookset.category.controller;

import com.nhnacademy.byeol23backend.bookset.category.dto.CategoryCreateRequest;
import com.nhnacademy.byeol23backend.bookset.category.dto.CategoryListResponse;
import com.nhnacademy.byeol23backend.bookset.category.dto.CategoryUpdateRequest;
import com.nhnacademy.byeol23backend.bookset.category.dto.CategoryUpdateResponse;
import com.nhnacademy.byeol23backend.bookset.category.service.CategoryCommandService;
import com.nhnacademy.byeol23backend.bookset.category.service.CategoryQueryService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class CategoryController {
    private final CategoryQueryService categoryQueryService;
    private final CategoryCommandService categoryCommandService;

    @PostMapping("/categories")
    @ResponseStatus(HttpStatus.CREATED)
    public void createCategory(@RequestBody CategoryCreateRequest createRequest) {
        categoryCommandService.createCategory(createRequest);
    }

    @PutMapping("/categories/{id}")
    @ResponseStatus(HttpStatus.OK)
    public CategoryUpdateResponse updateCategory(@PathVariable("id") Long id, @RequestBody CategoryUpdateRequest updateRequest) {
        return categoryCommandService.updateCategory(id, updateRequest);
    }

    @DeleteMapping("/categories/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteCategory(@PathVariable("id") Long id) {
        categoryCommandService.deleteCategory(id);
    }

    @GetMapping("/categories/roots")
    @ResponseStatus(HttpStatus.OK)
    public List<CategoryListResponse> getRoots() {
        return categoryQueryService.getRootCategories();
    }

    @GetMapping("/categories/{parentId}/children")
    @ResponseStatus(HttpStatus.OK)
    public List<CategoryListResponse> getChildren(@PathVariable("parentId") Long parentId) {
        return categoryQueryService.getSubCategories(parentId);
    }
}
