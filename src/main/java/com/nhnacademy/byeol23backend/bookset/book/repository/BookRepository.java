package com.nhnacademy.byeol23backend.bookset.book.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.nhnacademy.byeol23backend.bookset.book.domain.Book;

public interface BookRepository extends JpaRepository<Book, Long> {
}
