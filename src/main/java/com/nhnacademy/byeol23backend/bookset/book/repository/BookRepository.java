package com.nhnacademy.byeol23backend.bookset.book.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.nhnacademy.byeol23backend.bookset.book.domain.Book;
import com.nhnacademy.byeol23backend.image.dto.ImageUrlProjection;

@Repository
public interface BookRepository extends JpaRepository<Book, Long>, JdbcBookRepository {
	boolean existsByIsbn(String isbn);

	boolean existsByIsbnAndBookIdNot(String isbn, Long bookId);

	@Query("SELECT b FROM Book b JOIN FETCH b.bookImageUrls WHERE b.bookId = :id")
	Optional<Book> findBookWithImagesById(@Param("id") Long id);
}
