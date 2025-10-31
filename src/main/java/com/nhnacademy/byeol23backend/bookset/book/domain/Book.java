package com.nhnacademy.byeol23backend.bookset.book.domain;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import com.nhnacademy.byeol23backend.bookset.bookimage.domain.BookImage;
import com.nhnacademy.byeol23backend.bookset.publisher.domain.Publisher;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "books")
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
	//고책임님 피드백 이후 바뀐 ERD를 적용하여 작성하였습니다.
	@Column(name = "is_deleted", nullable = false)
	private boolean isDeleted;

	@OneToMany(mappedBy = "book", fetch = FetchType.LAZY, orphanRemoval = true)
	private List<BookImage> bookImageUrls = new ArrayList<>();
}