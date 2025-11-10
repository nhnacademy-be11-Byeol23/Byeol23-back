package com.nhnacademy.byeol23backend.bookset.publisher.domain.dto;

import jakarta.validation.constraints.NotNull;

public record PublisherCreateRequest(@NotNull String publisherName){

}
