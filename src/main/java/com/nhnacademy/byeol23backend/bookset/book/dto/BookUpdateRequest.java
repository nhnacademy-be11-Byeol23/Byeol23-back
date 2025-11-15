package com.nhnacademy.byeol23backend.bookset.book.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import org.springframework.web.multipart.MultipartFile;

import com.nhnacademy.byeol23backend.bookset.book.domain.BookStatus;

public record BookUpdateRequest(
	String bookName,
	String toc,
	String description,
	BigDecimal regularPrice,
	BigDecimal salePrice,
	LocalDate publishDate,
	Boolean isPack,
	BookStatus bookStatus,
	Long publisherId,
	List<Long> categoryIds,
	List<Long> tagIds,
	List<Long> contributorIds,
	List<MultipartFile> images
) {
	public BookUpdateRequest {
		if (images == null) {
			images = List.of();
		}
	}
}

