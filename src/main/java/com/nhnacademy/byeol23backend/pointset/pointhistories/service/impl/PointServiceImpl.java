package com.nhnacademy.byeol23backend.pointset.pointhistories.service.impl;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

import org.springframework.stereotype.Service;

import com.nhnacademy.byeol23backend.memberset.member.domain.Member;
import com.nhnacademy.byeol23backend.pointset.activatedpointpolicy.service.ActivatedPointPolicyService;
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
	private final ActivatedPointPolicyService activatedPointPolicyService;

	@Override
	public PointHistory offsetPoints(Member member, ReservedPolicy reservedPolicy) {
		final PointPolicy pointPolicy = activatedPointPolicyService.getActivatedPolicy(reservedPolicy);
		return pointInternalService.addPoints(member, pointPolicy, BigDecimal.ZERO);
	}

	@Override
	public PointHistory offsetPointsByOrder(Member member, BigDecimal orderAmount) {
		final PointPolicy orderPolicy = activatedPointPolicyService.getActivatedPolicy(ReservedPolicy.ORDER);
		BigDecimal points = member.getGrade()
			.getPointRate().add(orderPolicy.getSaveAmount()) //personal rate + base rate
			.multiply(orderAmount)
			.divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);

		return pointInternalService.addPoints(member, orderPolicy, points);
	}

	@Override
	public PointHistory offsetPointsWithExtra(Member member, ReservedPolicy reservedPolicy, BigDecimal extraAmount) {
		final PointPolicy pointPolicy = activatedPointPolicyService.getActivatedPolicy(reservedPolicy);
		return pointInternalService.addPoints(member, pointPolicy, extraAmount);
	}

	@Override
	public List<PointHistory> getPointHistoriesByMember(Member member) {
		return pointInternalService.getPointHistoriesByMember(member);
	}

	@Override
	public PointHistory cancelPoints(PointHistory pointHistory) {
		PointPolicy cancel = activatedPointPolicyService.getActivatedPolicy(ReservedPolicy.CANCEL);
		return pointInternalService.addPoints(
			pointHistory.getMemberId(),
			cancel,
			pointHistory.getPointAmount().negate()
		);
	}
}
