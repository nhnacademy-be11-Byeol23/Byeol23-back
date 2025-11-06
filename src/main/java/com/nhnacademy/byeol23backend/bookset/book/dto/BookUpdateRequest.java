package com.nhnacademy.byeol23backend.bookset.book.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

public record BookUpdateRequest(
	String bookName,
	String toc,
	String description,
	BigDecimal regularPrice,
	BigDecimal salePrice,
	LocalDate publishDate,
	Boolean isPack,
	String bookStatus,
	Integer stock,
	Long publisherId
) {
}

