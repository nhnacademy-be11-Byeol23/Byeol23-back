package com.nhnacademy.byeol23backend.bookset.book.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import org.springframework.web.multipart.MultipartFile;

import com.nhnacademy.byeol23backend.bookset.book.domain.BookStatus;

import jakarta.validation.constraints.NotEmpty;

public record BookCreateRequest(
	String bookName,
	String toc,
	String description,
	BigDecimal regularPrice,
	BigDecimal salePrice,
	String isbn,
	LocalDate publishDate,
	boolean isPack,
	BookStatus bookStatus,
	Integer stock,
	Long publisherId,
	@NotEmpty(message = "카테고리는 최소 한 개 이상 선택해야 합니다.")
	List<Long> categoryIds,
	List<Long> tagIds,
	List<Long> contributorIds,
	List<MultipartFile> images
) {

}

