package com.nhnacademy.byeol23backend.bookset.bookcategory.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.nhnacademy.byeol23backend.bookset.bookcategory.domain.BookCategory;
import com.nhnacademy.byeol23backend.bookset.category.domain.Category;

@Repository
public interface BookCategoryRepository extends JpaRepository<BookCategory, Long> {

	@Query("select case when count(c) > 0 then true else false end from BookCategory bc join Category c on bc.category.categoryId = c.categoryId where c.pathId like concat(:pathId, '%')")
	boolean existsByCategoryPathIdLike(@Param("pathId") String pathId);

	@Query("select bc.category from BookCategory bc where bc.book.bookId = :bookId")
	List<Category> findCategoriesByBookId(@Param("bookId") Long bookId);

	@Query("select bc from BookCategory bc join fetch bc.category where bc.book.bookId in :bookIds")
	List<BookCategory> findByBookIdsWithCategory(@Param("bookIds") List<Long> bookIds);

	@Modifying
	@Query("delete from BookCategory bc where bc.book.bookId = :bookId and bc.category.categoryId in :categoryIds")
	void deleteByBookIdAndCategoryIds(@Param("bookId") Long bookId, @Param("categoryIds") List<Long> categoryIds);

	@Modifying
	@Query("delete from BookCategory bc where bc.book.bookId = :bookId")
	void deleteByBookId(@Param("bookId") Long bookId);

	@Query("select count(bc) from BookCategory bc where bc.category.categoryId = :categoryId")
	Long countBookCategoriesByCategoryId(@Param("categoryId") Long categoryId);

	@Query("select bc.book.bookId " +
		"from BookCategory bc " +
		"where bc.category.categoryId = :categoryId " +
		"and bc.book.bookId = (" +
		"   select min(bc2.book.bookId) from BookCategory bc2 where bc2.category.categoryId = :categoryId" +
		")")
	Long findRepBookIdByCategoryId(@Param("categoryId") Long categoryId);
}
