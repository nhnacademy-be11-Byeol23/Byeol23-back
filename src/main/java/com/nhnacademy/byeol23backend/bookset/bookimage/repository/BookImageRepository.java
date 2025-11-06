package com.nhnacademy.byeol23backend.bookset.bookimage.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.nhnacademy.byeol23backend.bookset.bookimage.domain.BookImage;
import com.nhnacademy.byeol23backend.image.dto.ImageUrlProjection;

public interface BookImageRepository extends JpaRepository<BookImage, Long> {
	@Query(
		"SELECT new com.nhnacademy.byeol23backend.image.dto.ImageUrlProjection(bi.bookImageId, bi.bookImageUrl) " +
			"FROM BookImage bi " +
			"WHERE bi.book.bookId = :bookId"
	)
	List<ImageUrlProjection> findUrlsAndIdsByBookId(Long bookId);
}
