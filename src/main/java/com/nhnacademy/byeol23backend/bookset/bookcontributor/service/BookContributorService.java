package com.nhnacademy.byeol23backend.bookset.bookcontributor.service;

import java.util.List;

import com.nhnacademy.byeol23backend.bookset.book.domain.Book;
import com.nhnacademy.byeol23backend.bookset.contributor.domain.Contributor;

public interface BookContributorService {
	List<Contributor> getContributorsByBookId(Long bookId);

	void createBookContributors(Book book, List<Long> ContributorIds);

	void updateBookContributors(Book book, List<Long> ContributorIds);
}
