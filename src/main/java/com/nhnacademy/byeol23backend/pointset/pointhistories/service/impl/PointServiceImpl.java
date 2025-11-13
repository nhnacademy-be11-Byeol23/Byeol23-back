package com.nhnacademy.byeol23backend.pointset.pointhistories.service.impl;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

import org.springframework.stereotype.Service;

import com.nhnacademy.byeol23backend.memberset.member.domain.Member;
import com.nhnacademy.byeol23backend.pointset.pointpolicy.dto.ReservedPolicy;
import com.nhnacademy.byeol23backend.pointset.pointhistories.domain.PointHistory;
import com.nhnacademy.byeol23backend.pointset.pointhistories.service.PointService;
import com.nhnacademy.byeol23backend.pointset.pointpolicy.domain.PointPolicy;
import com.nhnacademy.byeol23backend.pointset.pointpolicy.repository.PointPolicyRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class PointServiceImpl implements PointService {
	private final PointInternalService pointInternalService;
	private final PointPolicyRepository pointPolicyRepository;

	@Override
	public PointHistory addPointsByReserved(Member member, ReservedPolicy reservedPolicy, BigDecimal orderAmount) {
		final PointPolicy pointPolicy = pointPolicyRepository.findByPointPolicyName(reservedPolicy.name())
			.orElseThrow(() -> new IllegalArgumentException("Invalid Point Policy Name: " + reservedPolicy.name()));
		return pointInternalService.addPoints(member, pointPolicy, orderAmount);
	}

	@Override
	public PointHistory addPointsWithPolicy(Member member, PointPolicy pointPolicy) {
		return pointInternalService.addPoints(member, pointPolicy, BigDecimal.ZERO);
	}

	@Override
	public PointHistory addPointsByOrder(Member member, BigDecimal orderAmount) {
		final PointPolicy orderPolicy = pointPolicyRepository.findByPointPolicyName(ReservedPolicy.ORDER.name())
			.orElseThrow(() -> new IllegalArgumentException("Invalid Point Policy Name: " + ReservedPolicy.ORDER.name()));

		BigDecimal points = member.getGrade()
			.getPointRate()
			.multiply(orderAmount)
			.divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);

		return pointInternalService.addPoints(member, orderPolicy, points);
	}

	@Override
	public List<PointHistory> getPointHistoriesByMember(Member member) {
		return pointInternalService.getPointHistoriesByMember(member);
	}
}
