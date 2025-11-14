package com.nhnacademy.byeol23backend.bookset.category.dto;

import com.nhnacademy.byeol23backend.bookset.category.domain.Category;

public record CategoryAllResponse(Long id, String pathName) {
    public static CategoryAllResponse from(Category category) {
        return new CategoryAllResponse(category.getCategoryId(), category.getPathName());
    }
}
