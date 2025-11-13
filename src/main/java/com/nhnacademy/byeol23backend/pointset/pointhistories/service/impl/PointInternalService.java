package com.nhnacademy.byeol23backend.pointset.pointhistories.service.impl;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.nhnacademy.byeol23backend.memberset.member.domain.Member;
import com.nhnacademy.byeol23backend.pointset.pointhistories.domain.PointHistory;
import com.nhnacademy.byeol23backend.pointset.pointhistories.exception.PointNotEnoughException;
import com.nhnacademy.byeol23backend.pointset.pointhistories.repository.PointHistoryRepository;
import com.nhnacademy.byeol23backend.pointset.pointpolicy.domain.PointPolicy;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@RequiredArgsConstructor
public class PointInternalService {
	private final PointHistoryRepository pointHistoryRepository;

	@Transactional(propagation = Propagation.MANDATORY)
	protected PointHistory addPoints(Member member, PointPolicy policy , BigDecimal additionalAmount) {
		PointHistory pointHistory = new PointHistory(member, policy, additionalAmount);
		member.setCurrentPoint(member.getCurrentPoint().add(policy.getSaveAmount().add(additionalAmount)));
		//point가 0보다 작으면 exception 발생
		if(!policy.getIsActive()){
			log.error("Point policy {} is inactive.", policy.getPointPolicyName());
			throw new IllegalStateException("Point policy is inactive: " + policy.getPointPolicyName());
		}
		if (member.getCurrentPoint().compareTo(BigDecimal.ZERO) < 0) {
			log.error("Member ID {} has insufficient points: {}", member.getMemberId(), member.getCurrentPoint());
			throw new PointNotEnoughException("Insufficient points for member ID: " + member.getMemberId());
		}
		pointHistoryRepository.save(pointHistory);
		return pointHistory;
	}

	@Transactional(propagation = Propagation.MANDATORY)
	protected List<PointHistory> getPointHistoriesByMember(Member member) {
		return pointHistoryRepository.findAllByMemberId((member));
	}
}
