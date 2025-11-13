package com.nhnacademy.byeol23backend.pointset.pointhistories.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.nhnacademy.byeol23backend.memberset.member.domain.Member;
import com.nhnacademy.byeol23backend.memberset.member.repository.MemberRepository;
import com.nhnacademy.byeol23backend.memberset.member.service.MemberService;
import com.nhnacademy.byeol23backend.pointset.pointhistories.domain.PointHistory;
import com.nhnacademy.byeol23backend.pointset.pointhistories.dto.PointHistoryDTO;
import com.nhnacademy.byeol23backend.pointset.pointhistories.service.PointService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/point-histories")
@RequiredArgsConstructor
public class HistoryController {
	private final PointService pointService;
	private final MemberRepository memberRepository;

	@GetMapping
	public List<PointHistoryDTO> getPointHistories(
		@RequestBody Member member
	) {
		member = memberRepository.findById(1L).orElse(null);
		List<PointHistory> histories = pointService.getPointHistoriesByMember(member);
		return histories.stream()
			.map(x->new PointHistoryDTO(x.getPointAmount(), x.getChangedAt(), x.getPointPolicy().getPointPolicyName()))
			.toList();
	}
}
