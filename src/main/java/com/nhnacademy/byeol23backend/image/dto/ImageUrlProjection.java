package com.nhnacademy.byeol23backend.image.dto;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public class ImageUrlProjection {
	private final Long urlId;
	private final String imageUrl;
}
