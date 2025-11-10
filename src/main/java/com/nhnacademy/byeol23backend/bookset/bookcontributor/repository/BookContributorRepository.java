package com.nhnacademy.byeol23backend.bookset.bookcontributor.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.nhnacademy.byeol23backend.bookset.bookcontributor.domain.BookContributor;

public interface BookContributorRepository extends JpaRepository<BookContributor, Long> {
	@Query("SELECT bc FROM BookContributor bc " +
		"JOIN FETCH bc.book " +
		"JOIN FETCH bc.contributor")
	List<BookContributor> getAllBookContributors();
}
