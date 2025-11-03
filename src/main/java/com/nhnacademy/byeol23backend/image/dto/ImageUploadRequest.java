package com.nhnacademy.byeol23backend.image.dto;

import com.nhnacademy.byeol23backend.image.domain.ImageDomain;

public record ImageUploadRequest(
	Long id,
	String imageUrl,
	ImageDomain imageDomain
) {
}
