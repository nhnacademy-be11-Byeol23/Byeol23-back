package com.nhnacademy.byeol23backend.bookset.bookcontributor.domain;

import com.nhnacademy.byeol23backend.bookset.book.domain.Book;
import com.nhnacademy.byeol23backend.bookset.contributor.domain.Contributor;

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
@Getter
@Table(name = "book_contributor")
@NoArgsConstructor
public class BookContributor {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "book_contributor_id")
	private Long bookContributorId;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "book_id", nullable = false)
	private Book book;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "contributor_id", nullable = false)
	private Contributor contributor;

	private BookContributor(Book book, Contributor contributor) {
		this.book = book;
		this.contributor = contributor;
	}

	public static BookContributor of(Book book, Contributor contributor) {
		return new BookContributor(book, contributor);
	}
}
