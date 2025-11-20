package com.nhnacademy.byeol23backend.pointset.pointhistories.service;

import java.math.BigDecimal;
import java.util.List;

import com.nhnacademy.byeol23backend.memberset.member.domain.Member;
import com.nhnacademy.byeol23backend.pointset.pointhistories.domain.PointHistory;
import com.nhnacademy.byeol23backend.pointset.pointpolicy.dto.ReservedPolicy;
import com.nhnacademy.byeol23backend.pointset.pointpolicy.domain.PointPolicy;

public interface PointService {
	PointHistory offsetPointsByReserved(Member member, ReservedPolicy reservedPolicy);

	PointHistory offsetPointsWithPolicy(Member member, PointPolicy pointPolicy);

	PointHistory offsetPointsByOrder(Member member, BigDecimal orderAmount);

	List<PointHistory> getPointHistoriesByMember(Member member);

	PointHistory cancelPoints(PointHistory pointHistory);
}
