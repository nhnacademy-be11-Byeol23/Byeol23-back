package com.nhnacademy.byeol23backend.bookset.book.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class BookRequestDto {
	private String bookName;
	private String contents;
	private String description;
	private BigDecimal regularPrice;
	private BigDecimal salePrice;
	private String isbn;
	private LocalDate publishDate;
	private Boolean isPack;
	private String bookStatus;
	private Integer stock;
	private Long publisherId;
	private String bookImg;
}

