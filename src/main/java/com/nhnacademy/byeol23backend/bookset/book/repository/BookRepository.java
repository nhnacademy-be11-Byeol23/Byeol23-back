package com.nhnacademy.byeol23backend.bookset.book.repository;

import java.util.List;
import java.util.Optional;

import com.nhnacademy.byeol23backend.bookset.book.dto.BookViewCount;
import com.nhnacademy.byeol23backend.bookset.book.dto.BookReview;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.nhnacademy.byeol23backend.bookset.book.domain.Book;

@Repository
public interface BookRepository extends JpaRepository<Book, Long>, JdbcBookRepository {
	boolean existsByIsbn(String isbn);

	boolean existsByIsbnAndBookIdNot(String isbn, Long bookId);

	@Query("SELECT b FROM Book b JOIN FETCH b.bookImageUrls WHERE b.bookId = :id")
	Optional<Book> findBookWithImagesById(@Param("id") Long id);

	@Modifying
	@Query("UPDATE Book b SET b.stock = b.stock - :quantity " +
		"WHERE b.bookId = :bookId And b.stock >= :quantity")
	int decreaseBookStock(@Param("bookId") Long bookId, @Param("quantity") Integer quantity);
	// 재고가 0이 되면 도서 상태(book status) -> 품절로

    @Query("select b from Book b join fetch b.publisher p where b.bookId = :bookId")
    Book queryBookWithPublisherById(@Param("bookId") Long bookId);

    @Query(value = """
select b.book_id as bookId, count(distinct r.review_id) as reviewCount, round(avg(r.review_rate), 1) as ratingAverage from books b 
    left join order_details od on b.book_id = od.book_id 
    left join reviews r on od.order_detail_id = r.order_detail_id where b.book_id = :bookId group by b.book_id
    """, nativeQuery = true)
    BookReview queryBookReview(@Param("bookId") Long bookId);

    @Query("select new com.nhnacademy.byeol23backend.bookset.book.dto.BookViewCount(b.bookId, b.viewCount) from Book b")
    List<BookViewCount> findAllBookViewCount();
}
