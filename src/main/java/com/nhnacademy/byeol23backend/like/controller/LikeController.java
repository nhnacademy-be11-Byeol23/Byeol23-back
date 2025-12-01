package com.nhnacademy.byeol23backend.like.controller;

import com.nhnacademy.byeol23backend.bookset.book.domain.Book;
import com.nhnacademy.byeol23backend.bookset.book.service.BookService;
import com.nhnacademy.byeol23backend.like.dto.LikeResponse;
import com.nhnacademy.byeol23backend.like.service.LikeService;
import com.nhnacademy.byeol23backend.memberset.member.domain.Member;
import com.nhnacademy.byeol23backend.memberset.member.repository.MemberRepository;
import com.nhnacademy.byeol23backend.utils.JwtParser;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/likes")
@RequiredArgsConstructor
public class LikeController {

    private final LikeService likeService;
    private final MemberRepository memberRepository;
    private final BookService bookService;
    private final JwtParser jwtParser;

    @GetMapping
    public ResponseEntity<List<LikeResponse>> getLikes(
        @CookieValue(name = "Access-Token", required = false) String accessToken
    ) {
        Long memberId = jwtParser.parseToken(accessToken).get("memberId", Long.class);
        List<LikeResponse> likes = likeService.getLikesByMemberId(memberId);
        return ResponseEntity.ok(likes);
    }

    @PostMapping("/{book-id}")
    public ResponseEntity<Void> toggleLike(
        @PathVariable("book-id") Long bookId,
        @CookieValue(name = "Access-Token", required = false) String accessToken
    ) {
        Long memberId = jwtParser.parseToken(accessToken).get("memberId", Long.class);
        Member member = memberRepository.getReferenceById(memberId);
        Book book = bookService.getBookWithPublisher(bookId);
        likeService.updateLike(member, book);
        return ResponseEntity.ok().build();
    }
}

