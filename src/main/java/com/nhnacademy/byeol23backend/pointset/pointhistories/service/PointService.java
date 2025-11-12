package com.nhnacademy.byeol23backend.pointset.pointhistories.service;

import java.math.BigDecimal;

import com.nhnacademy.byeol23backend.memberset.member.domain.Member;
import com.nhnacademy.byeol23backend.pointset.pointhistories.domain.PointHistory;
import com.nhnacademy.byeol23backend.pointset.pointpolicy.dto.ReservedPolicy;
import com.nhnacademy.byeol23backend.pointset.pointpolicy.domain.PointPolicy;

public interface PointService {
	PointHistory addPointsByReserved(Member member, ReservedPolicy reservedPolicy, BigDecimal orderAmount);

	PointHistory addPointsWithPolicy(Member member, PointPolicy pointPolicy);

	PointHistory addPointsByOrder(Member member, BigDecimal orderAmount);
}
