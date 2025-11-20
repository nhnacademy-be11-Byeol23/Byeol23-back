package com.nhnacademy.byeol23backend.bookset.book.repository;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.nhnacademy.byeol23backend.bookset.book.domain.Book;

@Repository
public interface BookRepository extends JpaRepository<Book, Long>, JdbcBookRepository {
	boolean existsByIsbn(String isbn);

	@Query("SELECT b FROM Book b JOIN FETCH b.bookImageUrls WHERE b.bookId = :id")
	Optional<Book> findBookWithImagesById(@Param("id") Long id);

	@Modifying
	@Query("UPDATE Book b SET b.stock = b.stock - :quantity " +
		"WHERE b.bookId = :bookId And b.stock >= :quantity")
	int decreaseBookStock(@Param("bookId") Long bookId, @Param("quantity") Integer quantity);
	// 재고가 0이 되면 도서 상태(book status) -> 품절로

	@Query("select b from Book b join fetch b.publisher p where b.bookId = :bookId")
	Book queryBookWithPublisherById(@Param("bookId") Long bookId);

	@EntityGraph(attributePaths = {"publisher"})
	Page<Book> findAll(Pageable pageable);
}
