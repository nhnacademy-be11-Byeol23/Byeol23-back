package com.nhnacademy.byeol23backend.bookset.book.domain;

import java.math.BigDecimal;
import java.time.LocalDate;

import org.hibernate.annotations.SQLDelete;

import com.nhnacademy.byeol23backend.bookset.book.dto.BookCreateRequest;
import com.nhnacademy.byeol23backend.bookset.book.dto.BookUpdateRequest;
import com.nhnacademy.byeol23backend.bookset.publisher.domain.Publisher;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@Table(name = "books")
@NoArgsConstructor
@SQLDelete(sql = "update books set is_deleted = true where book_id = ?")
public class Book {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "book_id")
	private Long bookId;

	@Column(name = "book_name", length = 200, nullable = false)
	private String bookName;

	@Column(name = "toc")
	private String toc;

	@Column(name = "description")
	private String description;

	@Column(name = "regular_price", nullable = false, precision = 10)
	private BigDecimal regularPrice;

	@Column(name = "sale_price", nullable = false, precision = 10)
	private BigDecimal salePrice;

	@Column(name = "isbn", nullable = false, length = 13, unique = true)
	private String isbn;

	private LocalDate publishDate;

	@Column(name = "is_pack", nullable = false)
	private boolean isPack;

	@Column(name = "book_status", nullable = false, length = 10)
	private String bookStatus;

	@Column(name = "stock", nullable = false)
	private Integer stock;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "publisher_id", nullable = false)
	private Publisher publisher;

	@Column(name = "is_deleted", nullable = false)
	private boolean isDeleted;

	public void createBook(BookCreateRequest request, Publisher publisher) {
		this.bookName = request.bookName();
		this.toc = request.toc();
		this.description = request.description();
		this.regularPrice = request.regularPrice();
		this.salePrice = request.salePrice();
		this.isbn = request.isbn();
		this.publishDate = request.publishDate();
		this.isPack = request.isPack();
		this.bookStatus = request.bookStatus();
		this.stock = request.stock();
		this.publisher = publisher;
		this.isDeleted = false;
	}

	public void updateBook(BookUpdateRequest request, Publisher publisher) {
		this.bookName = request.bookName();
		this.toc = request.toc();
		this.description = request.description();
		this.regularPrice = request.regularPrice();
		this.salePrice = request.salePrice();
		this.publishDate = request.publishDate();
		this.isPack = request.isPack();
		this.bookStatus = request.bookStatus();
		this.stock = request.stock();
		this.publisher = publisher;
	}
}