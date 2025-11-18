package com.nhnacademy.byeol23backend.memberset.member.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.nhnacademy.byeol23backend.memberset.member.domain.Member;

public interface MemberRepository extends JpaRepository<Member, Long> {
	List<Member> findAllByGrade_GradeId(Long gradeId);


    //회원 가입
    boolean existsByLoginId(String loginId);
    boolean existsByNickname(String nickname);
    boolean existsByEmail(String email);
    boolean existsByPhoneNumber(String phoneNumber);

    //회원 수정
    boolean existsByPhoneNumberAndMemberIdNot(String phoneNumber, Long memberId);
    boolean existsByEmailAndMemberIdNot(String email, Long memberId);


}
