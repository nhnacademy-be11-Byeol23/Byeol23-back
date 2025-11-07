package com.nhnacademy.byeol23backend.orderset.packaging.domain.dto;

import java.math.BigDecimal;

public record PackagingUpdateResponse(String packagingName,
									  BigDecimal packagingPrice,
									  String packagingImageUrl) {

}
