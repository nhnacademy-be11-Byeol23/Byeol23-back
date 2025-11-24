package com.nhnacademy.byeol23backend.pointset.pointhistories.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CookieValue;
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
import com.nhnacademy.byeol23backend.utils.JwtParser;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/api/point-histories")
@RequiredArgsConstructor
@Slf4j
public class HistoryController {
	private final PointService pointService;
	private final MemberRepository memberRepository;
	private final JwtParser jwtParser;

	@GetMapping
	public List<PointHistoryDTO> getPointHistories(@CookieValue(name = "Access-Token") String accessToken) {
		Long memberId = jwtParser.parseToken(accessToken).get("memberId",Long.class);
		Member member = memberRepository.findById(memberId).orElse(null);
		List<PointHistory> histories = pointService.getPointHistoriesByMember(member);
		log.info("getPointHistoriesByMember:{}", histories);
		return histories.stream()
			.map(x->new PointHistoryDTO(x.getPointAmount(), x.getChangedAt(), x.getPointPolicy().getPointPolicyName()))
			.toList();
	}
}
