package com.nhnacademy.byeol23backend.bookset.book.domain;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.hibernate.annotations.BatchSize;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;

import com.nhnacademy.byeol23backend.bookset.book.dto.BookCreateRequest;
import com.nhnacademy.byeol23backend.bookset.book.dto.BookUpdateRequest;
import com.nhnacademy.byeol23backend.bookset.bookcategory.domain.BookCategory;
import com.nhnacademy.byeol23backend.bookset.bookcontributor.domain.BookContributor;
import com.nhnacademy.byeol23backend.bookset.bookimage.domain.BookImage;
import com.nhnacademy.byeol23backend.bookset.booktag.domain.BookTag;
import com.nhnacademy.byeol23backend.bookset.publisher.domain.Publisher;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Entity
@Table(name = "books")
@NoArgsConstructor
@SQLDelete(sql = "update books set is_deleted = true where book_id = ?")
@Where(clause = "is_deleted = false")
public class Book {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "book_id")
	private Long bookId;

	@Column(name = "book_name", length = 200, nullable = false)
	private String bookName;

	@Column(name = "toc", columnDefinition = "text")
	private String toc;

	@Column(name = "description", columnDefinition = "text")
	private String description;

	@Column(name = "regular_price", nullable = false, precision = 10)
	private BigDecimal regularPrice;

	@Column(name = "sale_price", nullable = false, precision = 10)
	private BigDecimal salePrice;

	@Column(name = "isbn", nullable = false, length = 13, unique = true)
	private String isbn;

	private LocalDate publishDate;

	@Column(name = "is_pack", nullable = false, columnDefinition = "tinyint")
	private boolean isPack;

	@Column(name = "book_status", nullable = false, length = 10)
	@Enumerated(EnumType.STRING)
	private BookStatus bookStatus;

	@Setter
	@Column(name = "stock", nullable = false)
	private Integer stock;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "publisher_id", nullable = false)
	private Publisher publisher;

	@Column(name = "is_deleted", nullable = false, columnDefinition = "tinyint")
	private boolean isDeleted;

	@Column(name = "view_count", columnDefinition = "BIGINT DEFAULT 0")
	private long viewCount;

	@Column(name = "updated_at", nullable = false)
	private LocalDateTime updatedAt;

	@BatchSize(size = 100)
	@OneToMany(mappedBy = "book", fetch = FetchType.LAZY)
	private List<BookCategory> bookCategories = new ArrayList<>();

	@BatchSize(size = 100)
	@OneToMany(mappedBy = "book", fetch = FetchType.LAZY)
	private List<BookContributor> bookContributors = new ArrayList<>();

	@BatchSize(size = 100)
	@OneToMany(mappedBy = "book", fetch = FetchType.LAZY)
	private List<BookTag> bookTags = new ArrayList<>();

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
		this.updatedAt = LocalDateTime.now();
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
		this.publisher = publisher;
	}

	@OneToMany(mappedBy = "book", fetch = FetchType.LAZY, orphanRemoval = true)
	private List<BookImage> bookImageUrls = new ArrayList<>();
}