package com.nhnacademy.byeol23backend.pointset.pointhistories.service.impl;

import java.math.BigDecimal;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.nhnacademy.byeol23backend.memberset.member.domain.Member;
import com.nhnacademy.byeol23backend.memberset.member.repository.MemberRepository;
import com.nhnacademy.byeol23backend.pointset.pointhistories.repository.PointHistoryRepository;
import com.nhnacademy.byeol23backend.pointset.pointhistories.device.PointDevice;
import com.nhnacademy.byeol23backend.pointset.pointhistories.domain.PointHistory;
import com.nhnacademy.byeol23backend.pointset.pointhistories.service.PointService;
import com.nhnacademy.byeol23backend.pointset.pointpolicy.domain.PointPolicy;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class PointServiceImpl implements PointService {
	private final MemberRepository memberRepository;
	private final PointHistoryRepository pointHistoryRepository;
	private static PointPolicy ORDER_POINT_POLICY;
	@Transactional
	public void addPoint(
		long memberId,
		PointPolicy pointPolicy,
		PointDevice pointDevice
	) {
		Member member = memberRepository.findById(memberId).orElseThrow(
			() -> new IllegalArgumentException("해당 회원을 찾을 수 없습니다. 회원 ID: " + memberId)
		);
		PointHistory pointHistory = new PointHistory(
			member,
			pointPolicy
		);
		member.setCurrentPoint(member.getCurrentPoint().add(pointPolicy.getSaveAmount()));
		if(pointDevice != null) {
			pointDevice.setPointHistory(pointHistory);
		}
		pointHistoryRepository.save(pointHistory);
	}

	@Transactional
	public void addPointByOrder(
		long memberId,
		BigDecimal orderAmount
	){
		Member member = memberRepository.findById(memberId).orElseThrow(
			() -> new IllegalArgumentException("해당 회원을 찾을 수 없습니다. 회원 ID: " + memberId)
		);
		BigDecimal rate = member.getGrade().getPointRate();
		BigDecimal saveAmount = orderAmount.multiply(rate);
		
	}
}
