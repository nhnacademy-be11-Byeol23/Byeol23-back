package com.nhnacademy.byeol23backend.orderset.packaging.domain.dto;

import java.math.BigDecimal;

public record PackagingUpdateRequest(String packagingName,
									 BigDecimal packagingPrice) {
}
