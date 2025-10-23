package com.nhnacademy.byeol23backend.bookset.category.dto;

import com.nhnacademy.byeol23backend.bookset.category.domain.Category;

public record CategoryUpdateResponse(String name) {
    public static CategoryUpdateResponse from(Category category) {
        return new CategoryUpdateResponse(category.getCategoryName());
    }
}
