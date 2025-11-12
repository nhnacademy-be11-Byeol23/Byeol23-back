package com.nhnacademy.byeol23backend.bookset.category.dto;

import com.nhnacademy.byeol23backend.bookset.category.domain.Category;

import java.util.List;

public record CategoryTreeResponse(Long categoryId, String categoryName, List<CategoryTreeResponse> children) {
    public static CategoryTreeResponse from(Category category, int depth) {
        if(category == null) return null;

        List<CategoryTreeResponse> children = List.of();

        if(!category.getChildren().isEmpty() && depth >= 1) {
            children = category.getChildren().stream()
                    .map(child -> from(child, depth - 1))
                    .toList();
        }

        return new CategoryTreeResponse(category.getCategoryId(), category.getCategoryName(), children);
    }
}
