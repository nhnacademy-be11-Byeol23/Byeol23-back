package com.nhnacademy.byeol23backend.bookset.book.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

import com.nhnacademy.byeol23backend.bookset.book.domain.Book;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class BookResponseDto {
	private Long bookId;
	private String bookName;
	private String contents;
	private String description;
	private BigDecimal regularPrice;
	private BigDecimal salePrice;
	private String isbn;
	private LocalDate publishDate;
	private boolean isPack;
	private String bookStatus;
	private Integer stock;
	private String publisherName;
	private String bookImg;

	public BookResponseDto(Book book) {
		this.bookId = book.getBookId();
		this.bookName = book.getBookName();
		this.contents = book.getContents();
		this.description = book.getDescription();
		this.regularPrice = book.getRegularPrice();
		this.salePrice = book.getSalePrice();
		this.isbn = book.getIsbn();
		this.publishDate = book.getPublishDate();
		this.isPack = book.isPack();
		this.bookStatus = book.getBookStatus();
		this.stock = book.getStock();
		this.publisherName = book.getPublisher().toString();
		this.bookImg = book.getBookImg();
	}
}

