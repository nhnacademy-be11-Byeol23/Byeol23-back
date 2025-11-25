package com.nhnacademy.byeol23backend.memberset.member.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.nhnacademy.byeol23backend.memberset.member.domain.Member;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface MemberRepository extends JpaRepository<Member, Long>, MemberRepositoryCustom {


    //회원 가입
    boolean existsByLoginId(String loginId);
    boolean existsByNickname(String nickname);
    boolean existsByEmail(String email);
    boolean existsByPhoneNumber(String phoneNumber);

    //회원 수정
    boolean existsByPhoneNumberAndMemberIdNot(String phoneNumber, Long memberId);
    boolean existsByEmailAndMemberIdNot(String email, Long memberId);

	Optional<Member> getReferenceByMemberId(Long memberId);

    //특정 생일 회원 조회
    @Query(value = """
            SELECT m.member_id 
            FROM members m 
            WHERE 
                MONTH(m.birth_date) = :targetMonth 
                AND m.status = 'ACTIVE'
            """,
            nativeQuery = true)
    List<Long> findMembersByBirthdayMonth(@Param("targetMonth") int targetMonth);
}
