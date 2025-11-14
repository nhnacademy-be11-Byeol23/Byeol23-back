package com.nhnacademy.byeol23backend.pointset.pointhistories.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.nhnacademy.byeol23backend.memberset.member.domain.Member;
import com.nhnacademy.byeol23backend.pointset.pointhistories.domain.PointHistory;

public interface PointHistoryRepository extends JpaRepository<PointHistory, Long> {
	List<PointHistory> findAllByMemberId(Member member);
}
