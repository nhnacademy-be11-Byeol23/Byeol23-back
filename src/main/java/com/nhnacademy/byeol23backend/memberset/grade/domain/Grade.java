package com.nhnacademy.byeol23backend.memberset.grade.domain;

import java.math.BigDecimal;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "grades")
@Getter
@Setter
@NoArgsConstructor
public class Grade {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "grade_id")
	private Long gradeId;

	@Column(name = "grade_name", nullable = false, length = 10)
	private String gradeName;

	@Column(name = "criterion_price", nullable = false, precision = 10)
	private BigDecimal criterionPrice;

	@Column(name = "point_rate", nullable = false)
	private BigDecimal pointRate;

	public Grade(String gradeName, BigDecimal criterionPrice, BigDecimal pointRate) {
		this.gradeName = gradeName;
		this.criterionPrice = criterionPrice;
		this.pointRate = pointRate;
	}

	public void update(String gradeName, BigDecimal criterionPrice, BigDecimal pointRate) {
		this.gradeName = gradeName;
		this.criterionPrice = criterionPrice;
		this.pointRate = pointRate;
	}
}
