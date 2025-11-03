package com.nhnacademy.byeol23backend.image.dto;

import com.nhnacademy.byeol23backend.image.domain.ImageDomain;

public record ImageDeleteRequest(
	Long imageId,
	ImageDomain imageDomain
) {
}
