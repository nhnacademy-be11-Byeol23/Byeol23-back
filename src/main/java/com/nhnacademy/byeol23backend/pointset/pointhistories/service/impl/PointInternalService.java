package com.nhnacademy.byeol23backend.pointset.pointhistories.service.impl;

import com.nhnacademy.byeol23backend.memberset.member.domain.Member;
import com.nhnacademy.byeol23backend.pointset.pointhistories.domain.PointHistory;
import com.nhnacademy.byeol23backend.pointset.pointhistories.exception.PointNotEnoughException;
import com.nhnacademy.byeol23backend.pointset.pointhistories.repository.PointHistoryRepository;
import com.nhnacademy.byeol23backend.pointset.pointpolicy.domain.PointPolicy;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class PointInternalService {
	private final PointHistoryRepository pointHistoryRepository;

	@Transactional(propagation = Propagation.MANDATORY)
	protected PointHistory addPoints(Member member, PointPolicy policy , BigDecimal additionalAmount) {
		PointHistory pointHistory = new PointHistory(member, policy, additionalAmount);
		member.setCurrentPoint(member.getCurrentPoint().add(policy.getSaveAmount().add(additionalAmount)));
		if (member.getCurrentPoint().compareTo(BigDecimal.ZERO) < 0) {
			log.error("Member ID {} has insufficient points: {}", member.getMemberId(), member.getCurrentPoint());
			throw new PointNotEnoughException("Insufficient points for member ID: " + member.getMemberId());
		}
		pointHistoryRepository.save(pointHistory);
		return pointHistory;
	}

	protected List<PointHistory> getPointHistoriesByMember(Member member) {
		return pointHistoryRepository.findAllByMemberId(member);
	}
}
