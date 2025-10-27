package com.nhnacademy.byeol23backend.memberset.grade.service;

import java.math.BigDecimal;

import com.nhnacademy.byeol23backend.memberset.grade.dto.GradeDto;

public interface GradeService {
	void createGrade(String gradeName, BigDecimal criterionPrice);

	void updateGrade(String gradeName, BigDecimal criterionPrice);

	GradeDto getGrade(String gradeName);

	void deleteGrade(String gradeName);
}
