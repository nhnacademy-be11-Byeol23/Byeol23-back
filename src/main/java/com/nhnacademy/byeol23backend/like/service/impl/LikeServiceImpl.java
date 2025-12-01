package com.nhnacademy.byeol23backend.like.service.impl;

import com.nhnacademy.byeol23backend.bookset.book.domain.Book;
import com.nhnacademy.byeol23backend.bookset.book.dto.BookReview;
import com.nhnacademy.byeol23backend.bookset.book.service.BookService;
import com.nhnacademy.byeol23backend.bookset.bookcontributor.service.BookContributorService;
import com.nhnacademy.byeol23backend.bookset.contributor.domain.Contributor;
import com.nhnacademy.byeol23backend.like.domain.Like;
import com.nhnacademy.byeol23backend.like.dto.LikeResponse;
import com.nhnacademy.byeol23backend.like.repository.LikeRepository;
import com.nhnacademy.byeol23backend.like.service.LikeService;
import com.nhnacademy.byeol23backend.memberset.member.domain.Member;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class LikeServiceImpl implements LikeService {

    private final LikeRepository likeRepository;
    private final BookService bookService;
    private final BookContributorService bookContributorService;

    @Override
    public void updateLike(Member member, Book book) {
        boolean alreadyLiked = likeRepository.existsByMemberAndBook(member, book);

        if (!alreadyLiked) {
            likeRepository.save(new Like(member, book));
        } else {
            likeRepository.deleteByMemberAndBook(member, book);
        }
    }

    @Override
    @Transactional(readOnly = true)
    public List<LikeResponse> getLikesByMemberId(Long memberId) {
        List<Like> likes = likeRepository.findByMemberIdWithBookAndPublisher(memberId);
        
        return likes.stream()
            .map(like -> {
                Book book = like.getBook();
                
                // 이미지 URL 첫번째
                String imageUrl = null;
                if (book.getBookImageUrls() != null && !book.getBookImageUrls().isEmpty()) {
                    imageUrl = book.getBookImageUrls().get(0).getBookImageUrl();
                }
                
                // 저자 정보
                List<Contributor> contributors = bookContributorService.getContributorsByBookId(book.getBookId());
                String contributorsStr = contributors.stream()
                    .filter(c -> c.getContributorRole().name().equals("AUTHOR"))
                    .map(Contributor::getContributorName)
                    .collect(Collectors.joining(", "));
                
                // 리뷰 rate
                BookReview bookReview = bookService.getBookReview(book.getBookId());
                Long reviewCount = bookReview != null ? bookReview.reviewCount() : 0L;
                BigDecimal ratingAverage = bookReview != null && bookReview.ratingAverage() != null 
                    ? bookReview.ratingAverage() 
                    : BigDecimal.ZERO;
                
                return new LikeResponse(
                    book.getBookId(),
                    book.getBookName(),
                    imageUrl,
                    book.getPublisher().getPublisherName(),
                    contributorsStr,
                    book.getRegularPrice(),
                    book.getSalePrice(),
                    reviewCount,
                    ratingAverage
                );
            })
            .toList();
    }
}
