package com.nhnacademy.byeol23backend.memberset.member.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.nhnacademy.byeol23backend.memberset.member.domain.Member;

public interface MemberRepository extends JpaRepository<Member, Long> {
	List<Member> findAllByGrade_GradeId(Long gradeId);
}
