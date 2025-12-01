package com.nhnacademy.byeol23backend.like.service;

import com.nhnacademy.byeol23backend.bookset.book.domain.Book;
import com.nhnacademy.byeol23backend.like.dto.LikeResponse;
import com.nhnacademy.byeol23backend.memberset.member.domain.Member;

import java.util.List;

public interface LikeService {
    void updateLike(Member member, Book book);
    
    List<LikeResponse> getLikesByMemberId(Long memberId);
}
