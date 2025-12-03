package com.nhnacademy.byeol23backend.like.repository;

import com.nhnacademy.byeol23backend.bookset.book.domain.Book;
import com.nhnacademy.byeol23backend.like.domain.Like;
import com.nhnacademy.byeol23backend.memberset.member.domain.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface LikeRepository extends JpaRepository<Like, Long> {

    boolean existsByMemberAndBook(Member member, Book book);

    void deleteByMemberAndBook(Member member, Book book);

    @Query("SELECT l FROM Like l JOIN FETCH l.book b JOIN FETCH b.publisher WHERE l.member.memberId = :memberId")
    List<Like> findByMemberIdWithBookAndPublisher(@Param("memberId") Long memberId);
}
