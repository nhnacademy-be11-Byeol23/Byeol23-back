package com.nhnacademy.byeol23backend.memberset.grade.service;

import java.math.BigDecimal;
import java.util.List;

import com.nhnacademy.byeol23backend.memberset.grade.dto.AllGradeResponse;
import com.nhnacademy.byeol23backend.memberset.grade.dto.GradeDto;

public interface GradeService {
	void createGrade(String gradeName, BigDecimal criterionPrice, BigDecimal pointRate);

	void updateGrade(String gradeName, BigDecimal criterionPrice, BigDecimal pointRate);

	GradeDto getGrade(String gradeName);

	void deleteGrade(String gradeName);

	List<AllGradeResponse> getAllGrades();
}
