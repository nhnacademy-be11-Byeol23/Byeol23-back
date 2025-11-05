package com.nhnacademy.byeol23backend.bookset.bookimage.domain;

import com.nhnacademy.byeol23backend.bookset.book.domain.Book;

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

@Entity
@Table(name = "book_image")
@NoArgsConstructor
@Getter
public class BookImage {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "book_image_id")
	private Long bookImageId;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "book_id", nullable = false)
	private Book book;

	@Column(name = "book_image_url")
	private String bookImageUrl;

	public BookImage(Book book, String bookImageUrl) {
		this.book = book;
		this.bookImageUrl = bookImageUrl;
	}

}
