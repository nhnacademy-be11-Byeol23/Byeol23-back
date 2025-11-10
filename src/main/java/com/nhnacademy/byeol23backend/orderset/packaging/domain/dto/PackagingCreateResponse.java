package com.nhnacademy.byeol23backend.orderset.packaging.domain.dto;

import java.math.BigDecimal;

public record PackagingCreateResponse(Long packagingId,
									  String packagingName,
									  BigDecimal packagingPrice,
									  String packagingImgUrl) {
}
