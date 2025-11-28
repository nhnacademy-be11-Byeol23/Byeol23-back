package com.nhnacademy.byeol23backend.memberset.grade.dto;

import java.math.BigDecimal;

public record AllGradeResponse(
	String gradeName,
	BigDecimal criterionPrice,
	BigDecimal pointRate
) {
}
