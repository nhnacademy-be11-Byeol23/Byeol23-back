package com.nhnacademy.byeol23backend.pointset.pointhistories.service.impl;

import java.math.BigDecimal;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.nhnacademy.byeol23backend.memberset.member.domain.Member;
import com.nhnacademy.byeol23backend.memberset.member.repository.MemberRepository;
import com.nhnacademy.byeol23backend.pointset.pointhistories.repository.PointHistoryRepository;
import com.nhnacademy.byeol23backend.pointset.pointhistories.domain.PointHistory;
import com.nhnacademy.byeol23backend.pointset.pointhistories.service.PointService;
import com.nhnacademy.byeol23backend.pointset.pointpolicy.domain.PointPolicy;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class PointServiceImpl implements PointService {
	private final MemberRepository memberRepository;
	private final PointHistoryRepository pointHistoryRepository;

	@Transactional(propagation = Propagation.MANDATORY)
	public void addPointsToMember(Member member, PointPolicy pointPolicy) {
		PointHistory pointHistory = new PointHistory(member, pointPolicy);
		pointHistoryRepository.save(pointHistory);
		member.setCurrentPoint(member.getCurrentPoint().add(pointPolicy.getSaveAmount()));
	}
}
