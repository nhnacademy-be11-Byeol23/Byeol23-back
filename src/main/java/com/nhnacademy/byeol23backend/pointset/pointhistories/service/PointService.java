package com.nhnacademy.byeol23backend.pointset.pointhistories.service;

import java.math.BigDecimal;
import java.util.List;

import com.nhnacademy.byeol23backend.memberset.member.domain.Member;
import com.nhnacademy.byeol23backend.pointset.pointhistories.domain.PointHistory;
import com.nhnacademy.byeol23backend.pointset.pointpolicy.dto.ReservedPolicy;

public interface PointService {
	PointHistory offsetPoints(Member member, ReservedPolicy reservedPolicy); //이거 사용하세요

	PointHistory offsetPointsByOrder(Member member, BigDecimal orderAmount); //이것도 유현님만

	PointHistory offsetPointsWithExtra(Member member, ReservedPolicy reservedPolicy, BigDecimal extraAmount); //이건 유현님만 사용할 것 같아요

	List<PointHistory> getPointHistoriesByMember(Member member);  //이건 view용이에요

	PointHistory cancelPoints(PointHistory pointHistory); //이것도 유현님만
}
