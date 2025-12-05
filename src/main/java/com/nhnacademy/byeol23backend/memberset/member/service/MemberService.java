package com.nhnacademy.byeol23backend.memberset.member.service;

import java.math.BigDecimal;

import com.nhnacademy.byeol23backend.memberset.member.domain.Member;
import com.nhnacademy.byeol23backend.memberset.member.dto.*;

/**
 * 회원 관련 비즈니스 로직을 처리하는 서비스 인터페이스
 */
public interface MemberService {
	/**
	 * 새로운 회원을 생성합니다.
	 * @param request 회원가입 요청 정보
	 * @return 생성된 회원 정보
	 */
	MemberCreateResponse createMember(MemberCreateRequest request);

	/**
	 * 회원 ID로 회원 정보를 조회합니다.
	 * @param memberId 회원 ID
	 * @return 회원 마이페이지 정보
	 */
	MemberMyPageResponse getMember(Long memberId);

	/**
	 * 회원 정보를 수정합니다.
	 * @param memberId 회원 ID
	 * @param request 수정할 회원 정보
	 * @return 수정된 회원 정보
	 */
	MemberUpdateResponse updateMember(Long memberId, MemberUpdateRequest request);

	/**
	 * 회원 비밀번호를 변경합니다.
	 * @param memberId 회원 ID
	 * @param request 비밀번호 변경 요청 정보
	 * @return 비밀번호 변경 결과
	 */
	MemberPasswordUpdateResponse updateMemberPassword(Long memberId, MemberPasswordUpdateRequest request);

	/**
	 * 비활성화된 회원을 재활성화합니다.
	 * @param memberId 회원 ID
	 */
	void reactivateMember(Long memberId);

	/**
	 * 회원을 탈퇴 처리합니다 (Soft Delete).
	 * @param memberId 회원 ID
	 */
	void deleteMember(Long memberId);

	/**
	 * 회원의 포인트를 업데이트합니다.
	 * @param memberId 회원 ID
	 * @param point 변경할 포인트
	 */
	void updateMemberPoint(Long memberId, BigDecimal point);

	/**
	 * 로그인 ID의 중복 여부를 확인합니다.
	 * @param loginId 확인할 로그인 ID
	 * @return 중복 여부 (true: 중복됨, false: 사용 가능)
	 */
	boolean checkIdDuplicated(String loginId);

	/**
	 * 회원 엔티티를 프록시로 조회합니다 (지연 로딩용).
	 * @param memberId 회원 ID
	 * @return 회원 엔티티 프록시
	 */
	Member getMemberProxy(Long memberId);

	/**
	 * 3개월 이상 로그인하지 않은 회원을 비활성화합니다.
	 */
	void deactivateMembersNotLoggedInFor3Months();

	/**
	 * 회원 정보(ID, 닉네임, 전화번호, 이메일)의 중복 여부를 확인합니다.
	 * @param request 중복 확인 요청 정보
	 * @return 각 필드별 중복 여부
	 */
	ValueDuplicatedResponse checkInfoDuplicated(ValueDuplicatedRequest request);
}
