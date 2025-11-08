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

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class PointServiceImpl implements PointService {
	private final MemberRepository memberRepository;
	private final PointHistoryRepository pointHistoryRepository;

	@Transactional
	public void addPointsToMember(Long memberId, PointDevice pointDevice) {
		Member member = memberRepository.findById(memberId)
			.orElseThrow(() -> new IllegalArgumentException("Member not found with id: " + memberId));

		
	}
}
