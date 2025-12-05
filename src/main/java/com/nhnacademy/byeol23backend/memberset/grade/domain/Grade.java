package com.nhnacademy.byeol23backend.memberset.grade.domain;

import java.math.BigDecimal;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Entity
@Table(name = "grades")
@AllArgsConstructor
@NoArgsConstructor
public class Grade {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "grade_id")
	private Long gradeId;

	@Setter
	@Column(name = "grade_name", nullable = false, length = 10)
	private String gradeName;

	@Setter
	@Column(name = "criterion_price", nullable = false, precision = 10)
	private BigDecimal criterionPrice;

	@Setter
	@Column(name = "point_rate", nullable = false, precision = 5, scale = 2)
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

