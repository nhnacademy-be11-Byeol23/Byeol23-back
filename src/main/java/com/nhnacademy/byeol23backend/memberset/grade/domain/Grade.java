package com.nhnacademy.byeol23backend.memberset.grade.domain;

import java.math.BigDecimal;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "grades")
public class Grade {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "grade_id")
	private Long gradeId;

	@Column(name = "grade_name", nullable = false, length = 10)
	private String gradeName;

	@Column(name = "criterion_price", nullable = false, precision = 10)
	private BigDecimal criterionPrice;

	@Column(name = "point_rate", nullable = false, precision = 5, scale = 2)
	private BigDecimal pointRate;
}

