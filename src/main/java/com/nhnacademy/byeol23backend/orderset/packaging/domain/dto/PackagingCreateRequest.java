package com.nhnacademy.byeol23backend.orderset.packaging.domain.dto;

import java.math.BigDecimal;

public record PackagingCreateRequest(String packagingName,
									 BigDecimal packagingPrice) {

}
