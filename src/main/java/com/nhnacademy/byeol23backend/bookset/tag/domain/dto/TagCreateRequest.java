package com.nhnacademy.byeol23backend.bookset.tag.domain.dto;

import jakarta.validation.constraints.NotNull;

public record TagCreateRequest(@NotNull String tagName){

}
