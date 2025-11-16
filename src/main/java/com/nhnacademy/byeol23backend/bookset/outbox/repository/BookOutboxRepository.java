package com.nhnacademy.byeol23backend.bookset.outbox.repository;

import com.nhnacademy.byeol23backend.bookset.outbox.BookOutbox;
import org.springframework.data.repository.CrudRepository;

public interface BookOutboxRepository extends CrudRepository<BookOutbox, Long> {
}
