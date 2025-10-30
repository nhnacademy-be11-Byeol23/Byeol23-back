package com.nhnacademy.byeol23backend.bookset.bookimage.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.nhnacademy.byeol23backend.bookset.bookimage.domain.BookImage;

public interface BookImageRepository extends JpaRepository<BookImage, Long> {
}
