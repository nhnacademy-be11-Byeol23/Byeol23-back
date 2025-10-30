package com.nhnacademy.byeol23backend.bookset.category.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CategoryCreateRequest(
        @NotBlank(message = "카테고리명은 필수 값입니다.")
        @Size(max = 50)
        String name,
        Long parentId) {
}
