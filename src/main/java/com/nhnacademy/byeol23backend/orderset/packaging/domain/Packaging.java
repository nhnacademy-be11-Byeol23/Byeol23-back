package com.nhnacademy.byeol23backend.orderset.packaging.domain;

import java.math.BigDecimal;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Getter
@Entity
@Table(name = "packaging")
public class Packaging {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "packaging_id")
	private Long packagingId;

	@Column(name = "packaging_name", nullable = false, length = 30)
	private String packagingName;

	@Setter
	@Column(name = "packaging_img")
	private String packagingImg;

	@Column(name = "packaging_price", nullable = false, precision = 10)
	private BigDecimal packagingPrice;
}
