package com.nhnacademy.byeol23backend.bookset.book.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import org.springframework.web.multipart.MultipartFile;

public record BookCreateRequest(
	String bookName,
	String toc,
	String description,
	BigDecimal regularPrice,
	BigDecimal salePrice,
	String isbn,
	LocalDate publishDate,
	boolean isPack,
	String bookStatus,
	Integer stock,
	Long publisherId,
	List<Long> categoryIds,
	List<Long> tagIds,
	List<Long> contributorIds,
	List<MultipartFile> images
) {
	public BookCreateRequest {
		if (images == null) {
			images = List.of();
		}
	}
}

