package com.nhnacademy.byeol23backend.memberset.member.service;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import com.nhnacademy.byeol23backend.memberset.member.domain.Member;
import com.nhnacademy.byeol23backend.memberset.member.dto.*;

public interface MemberService {
	MemberCreateResponse createMember(MemberCreateRequest request);

	MemberMyPageResponse getMember(Long memberId);

	MemberUpdateResponse updateMember(Long memberId, MemberUpdateRequest request);

	MemberPasswordUpdateResponse updateMemberPassword(Long memberId, MemberPasswordUpdateRequest request);

	void reactivateMember(Long memberId);

	void deleteMember(Long memberId);

	void updateMemberPoint(Long memberId, BigDecimal point);

	boolean checkIdDuplicated(String loginId);

	//member proxy
	Member getMemberProxy(Long memberId);

    void deactivateMembersNotLoggedInFor3Months();

	public ValueDuplicatedResponse checkInfoDuplicated(ValueDuplicatedRequest request);
}
