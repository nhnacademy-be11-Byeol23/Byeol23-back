package com.nhnacademy.byeol23backend.bookset.book.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

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
	Long publisherId,
	List<Long> categoryIds,
	List<Long> tagIds,
	List<Long> contributorIds
) {
}

