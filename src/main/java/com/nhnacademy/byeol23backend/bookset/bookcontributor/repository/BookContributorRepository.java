package com.nhnacademy.byeol23backend.bookset.bookcontributor.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.nhnacademy.byeol23backend.bookset.bookcontributor.domain.BookContributor;
import com.nhnacademy.byeol23backend.bookset.contributor.domain.Contributor;

public interface BookContributorRepository extends JpaRepository<BookContributor, Long> {
	@Query("SELECT bc FROM BookContributor bc " +
		"JOIN FETCH bc.book " +
		"JOIN FETCH bc.contributor")
	List<BookContributor> getAllBookContributors();

	@Query("select bc.contributor from BookContributor bc where bc.book.bookId = :bookId")
	List<Contributor> findContributorByBookId(@Param("bookId") Long bookId);

	@Query("select bc from BookContributor bc join fetch bc.contributor where bc.book.bookId in :bookIds")
	List<BookContributor> findByBookIdsWithContributor(@Param("bookIds") List<Long> bookIds);

	@Modifying
	@Query("delete from BookContributor bc where bc.book.bookId = :bookId and bc.contributor.contributorId in :contributorIds")
	void deleteByBookIdAndContributorIds(@Param("bookId") Long bookId,
		@Param("contributorIds") List<Long> contributorIds);

	@Modifying
	@Query("delete from BookContributor bc where bc.book.bookId = :bookId")
	void deleteByBookId(@Param("bookId") Long bookId);
}
