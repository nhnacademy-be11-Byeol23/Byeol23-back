package com.nhnacademy.byeol23backend.bookset.book.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.nhnacademy.byeol23backend.bookset.book.domain.Book;

@Repository
public interface BookRepository extends JpaRepository<Book, Long>, JdbcBookRepository {
	boolean existsByIsbn(String isbn);

	boolean existsByIsbnAndBookIdNot(String isbn, Long bookId);
}
