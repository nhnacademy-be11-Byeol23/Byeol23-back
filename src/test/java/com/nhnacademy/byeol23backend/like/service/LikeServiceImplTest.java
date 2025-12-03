package com.nhnacademy.byeol23backend.like.service;

import com.nhnacademy.byeol23backend.bookset.book.domain.Book;
import com.nhnacademy.byeol23backend.bookset.book.dto.BookReview;
import com.nhnacademy.byeol23backend.bookset.book.service.BookService;
import com.nhnacademy.byeol23backend.bookset.bookcontributor.service.BookContributorService;
import com.nhnacademy.byeol23backend.bookset.contributor.domain.Contributor;
import com.nhnacademy.byeol23backend.like.domain.Like;
import com.nhnacademy.byeol23backend.like.dto.LikeResponse;
import com.nhnacademy.byeol23backend.like.repository.LikeRepository;
import com.nhnacademy.byeol23backend.like.service.impl.LikeServiceImpl;
import com.nhnacademy.byeol23backend.memberset.member.domain.Member;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.never;

@ExtendWith(MockitoExtension.class)
class LikeServiceImplTest {

    @Mock
    private LikeRepository likeRepository;

    @Mock
    private BookService bookService;

    @Mock
    private BookContributorService bookContributorService;

    @InjectMocks
    private LikeServiceImpl likeService;

    @Mock
    private Member member;

    @Mock
    private Book book;

    @Test
    @DisplayName("updateLike - 아직 좋아요가 없으면 저장")
    void updateLike_savesWhenNotLiked() {
        // given
        given(likeRepository.existsByMemberAndBook(member, book)).willReturn(false);

        // when
        likeService.updateLike(member, book);

        // then
        ArgumentCaptor<Like> likeCaptor = ArgumentCaptor.forClass(Like.class);
        then(likeRepository).should().save(likeCaptor.capture());
        then(likeRepository).should(never()).deleteByMemberAndBook(member, book);

        Like saved = likeCaptor.getValue();
        assertThat(saved.getMember()).isEqualTo(member);
        assertThat(saved.getBook()).isEqualTo(book);
    }

    @Test
    @DisplayName("updateLike - 이미 좋아요가 있으면 삭제")
    void updateLike_deletesWhenAlreadyLiked() {
        // given
        given(likeRepository.existsByMemberAndBook(member, book)).willReturn(true);

        // when
        likeService.updateLike(member, book);

        // then
        then(likeRepository).should().deleteByMemberAndBook(member, book);
        then(likeRepository).should(never()).save(org.mockito.Mockito.any(Like.class));
    }

    @Test
    @DisplayName("getLikesByMemberId - Like 엔티티를 LikeResponse DTO로 변환")
    void getLikesByMemberId_mapsToResponse() {
        // given
        Long memberId = 1L;

        Like like = org.mockito.Mockito.mock(Like.class);
        Book likeBook = org.mockito.Mockito.mock(Book.class);

        given(like.getBook()).willReturn(likeBook);
        given(likeBook.getBookId()).willReturn(10L);
        given(likeBook.getBookName()).willReturn("테스트 도서");
        given(likeBook.getRegularPrice()).willReturn(new BigDecimal("10000"));
        given(likeBook.getSalePrice()).willReturn(new BigDecimal("9000"));

        // publisher
        com.nhnacademy.byeol23backend.bookset.publisher.domain.Publisher publisher =
                org.mockito.Mockito.mock(com.nhnacademy.byeol23backend.bookset.publisher.domain.Publisher.class);
        given(likeBook.getPublisher()).willReturn(publisher);
        given(publisher.getPublisherName()).willReturn("테스트 출판사");

        // 이미지 URL 리스트가 비어 있다고 가정 (null 혹은 빈 리스트에 대한 방어 로직 검증 목적)
        given(likeBook.getBookImageUrls()).willReturn(List.of());

        // 저자 정보
        Contributor author = org.mockito.Mockito.mock(Contributor.class);
        given(author.getContributorRole()).willReturn(
                com.nhnacademy.byeol23backend.bookset.contributor.domain.ContributorRole.AUTHOR);
        given(author.getContributorName()).willReturn("테스트 작가");
        given(bookContributorService.getContributorsByBookId(10L)).willReturn(List.of(author));

        // 리뷰 정보
        BookReview bookReview = new BookReview(10L, 5L, new BigDecimal("4.5"));
        given(bookService.getBookReview(10L)).willReturn(bookReview);

        given(likeRepository.findByMemberIdWithBookAndPublisher(memberId))
                .willReturn(List.of(like));

        // when
        List<LikeResponse> result = likeService.getLikesByMemberId(memberId);

        // then
        assertThat(result).hasSize(1);
        LikeResponse response = result.get(0);
        assertThat(response.bookId()).isEqualTo(10L);
        assertThat(response.bookName()).isEqualTo("테스트 도서");
        assertThat(response.publisherName()).isEqualTo("테스트 출판사");
        assertThat(response.contributors()).isEqualTo("테스트 작가");
        assertThat(response.reviewCount()).isEqualTo(5L);
        assertThat(response.ratingAverage()).isEqualTo(new BigDecimal("4.5"));
    }
}


