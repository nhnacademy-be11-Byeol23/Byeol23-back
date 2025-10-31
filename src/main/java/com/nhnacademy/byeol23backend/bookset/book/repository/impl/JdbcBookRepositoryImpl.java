package com.nhnacademy.byeol23backend.bookset.book.repository.impl;

import com.nhnacademy.byeol23backend.bookset.book.repository.JdbcBookRepository;
import com.nhnacademy.byeol23backend.bookset.book.service.impl.BookViewCountSyncServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class JdbcBookRepositoryImpl implements JdbcBookRepository {
    private final JdbcTemplate jdbcTemplate;

    @Override
    public void bookViewCountBatchUpdate(List<BookViewCountSyncServiceImpl.BookViewCount> bookViewCounts) {
        jdbcTemplate.batchUpdate("update books set view_count = view_count + ? where book_id = ?", bookViewCounts, 100, (ps, bookViewCount) -> {
            ps.setLong(1, bookViewCount.viewCount());
            ps.setLong(2, bookViewCount.bookId());
        });
    }
}
