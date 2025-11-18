package com.nhnacademy.byeol23backend.bookset.book.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import com.nhnacademy.byeol23backend.bookset.book.domain.BookStatus;
import com.nhnacademy.byeol23backend.bookset.category.dto.CategoryLeafResponse;
import com.nhnacademy.byeol23backend.bookset.contributor.domain.dto.AllContributorResponse;
import com.nhnacademy.byeol23backend.bookset.publisher.domain.dto.AllPublishersInfoResponse;
import com.nhnacademy.byeol23backend.bookset.tag.domain.dto.AllTagsInfoResponse;
import com.nhnacademy.byeol23backend.image.dto.GetUrlResponse;

public record BookResponse(
	Long bookId,
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
	AllPublishersInfoResponse publisher,
	boolean isDeleted,
	List<CategoryLeafResponse> categories,
	List<AllTagsInfoResponse> tags,
	List<AllContributorResponse> contributors,
	List<GetUrlResponse> images
) {
}
