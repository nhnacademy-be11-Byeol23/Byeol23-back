package com.nhnacademy.byeol23backend.bookset.outbox.repository;

import org.springframework.data.repository.CrudRepository;

import com.nhnacademy.byeol23backend.bookset.outbox.BookOutbox;

public interface BookOutboxRepository extends CrudRepository<BookOutbox, Long> {
}
