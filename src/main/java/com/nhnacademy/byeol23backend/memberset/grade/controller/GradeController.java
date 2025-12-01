package com.nhnacademy.byeol23backend.memberset.grade.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.nhnacademy.byeol23backend.memberset.grade.dto.AllGradeResponse;
import com.nhnacademy.byeol23backend.memberset.grade.service.GradeService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/grade")
@RequiredArgsConstructor
public class GradeController {

	private final GradeService gradeService;

	@GetMapping
	public ResponseEntity<List<AllGradeResponse>> getAllGrade(){
		return ResponseEntity.ok().body(gradeService.getAllGrades());
	}
}
