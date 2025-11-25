package com.nhnacademy.byeol23backend.bookset.booktag.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.nhnacademy.byeol23backend.bookset.booktag.domain.BookTag;
import com.nhnacademy.byeol23backend.bookset.tag.domain.Tag;

public interface BookTagRepository extends JpaRepository<BookTag, Long> {
	@Query("select bt.tag from BookTag bt where bt.book.bookId = :bookId")
	List<Tag> findTagsByBookId(@Param("bookId") Long bookId);

	@Query("select bt from BookTag bt join fetch bt.tag where bt.book.bookId in :bookIds")
	List<BookTag> findByBookIdsWithTag(@Param("bookIds") List<Long> bookIds);

	@Modifying
	@Query("delete from BookTag bt where bt.book.bookId = :bookId and bt.tag.tagId in :tagIds")
	void deleteByBookIdAndTagIds(@Param("bookId") Long bookId, @Param("tagIds") List<Long> tagIds);

	@Modifying
	@Query("delete from BookTag bt where bt.book.bookId = :bookId")
	void deleteByBookId(@Param("bookId") Long bookId);

	@Modifying
	@Query("delete from BookTag bt where bt.tag.tagId = :tagId")
	void deleteByTagId(@Param("tagId") Long tagId);
}
