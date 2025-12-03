package com.nhnacademy.byeol23backend.like.controller;

import com.nhnacademy.byeol23backend.bookset.book.domain.Book;
import com.nhnacademy.byeol23backend.bookset.book.service.BookService;
import com.nhnacademy.byeol23backend.like.dto.LikeResponse;
import com.nhnacademy.byeol23backend.like.service.LikeService;
import com.nhnacademy.byeol23backend.memberset.member.domain.Member;
import com.nhnacademy.byeol23backend.memberset.member.repository.MemberRepository;
import com.nhnacademy.byeol23backend.utils.JwtParser;
import io.jsonwebtoken.Claims;
import jakarta.servlet.http.Cookie;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(LikeController.class)
class LikeControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private LikeService likeService;

    @MockBean
    private MemberRepository memberRepository;

    @MockBean
    private BookService bookService;

    @MockBean
    private JwtParser jwtParser;

    @Test
    @DisplayName("GET /api/likes - 회원의 좋아요 목록 조회")
    void getLikes_returnsList() throws Exception {
        // given
        String accessToken = "member-access-token";
        Long memberId = 1L;

        Claims claims = Mockito.mock(Claims.class);
        given(claims.get(eq("memberId"), eq(Long.class))).willReturn(memberId);
        given(jwtParser.parseToken(eq(accessToken))).willReturn(claims);

        List<LikeResponse> responses = List.of(
                new LikeResponse(
                        1L,
                        "book",
                        "url",
                        "publisher",
                        "author",
                        BigDecimal.TEN,
                        BigDecimal.ONE,
                        0L,
                        BigDecimal.ZERO
                )
        );

        given(likeService.getLikesByMemberId(memberId)).willReturn(responses);

        // when & then
        mockMvc.perform(get("/api/likes")
                        .cookie(new Cookie("Access-Token", accessToken))
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        then(likeService).should().getLikesByMemberId(memberId);
    }

    @Test
    @DisplayName("POST /api/likes/{book-id} - 좋아요 토글")
    void toggleLike_updatesLike() throws Exception {
        // given
        String accessToken = "member-access-token";
        Long memberId = 1L;
        Long bookId = 10L;

        Claims claims = Mockito.mock(Claims.class);
        given(claims.get(eq("memberId"), eq(Long.class))).willReturn(memberId);
        given(jwtParser.parseToken(eq(accessToken))).willReturn(claims);

        Member member = Mockito.mock(Member.class);
        given(memberRepository.getReferenceById(memberId)).willReturn(member);

        Book book = Mockito.mock(Book.class);
        given(bookService.getBookWithPublisher(bookId)).willReturn(book);

        // when & then
        mockMvc.perform(post("/api/likes/{book-id}", bookId)
                        .cookie(new Cookie("Access-Token", accessToken)))
                .andExpect(status().isOk());

        then(likeService).should().updateLike(member, book);
    }
}


