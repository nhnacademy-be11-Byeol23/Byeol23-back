package com.nhnacademy.byeol23backend.memberset.member.service.impl;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Objects;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.nhnacademy.byeol23backend.cartset.cart.domain.Cart;
import com.nhnacademy.byeol23backend.cartset.cart.repository.CartRepository;
import com.nhnacademy.byeol23backend.couponset.coupon.dto.BirthdayCouponIssueRequestDto;
import com.nhnacademy.byeol23backend.memberset.address.domain.Address;
import com.nhnacademy.byeol23backend.memberset.address.dto.AddressResponse;
import com.nhnacademy.byeol23backend.memberset.address.repository.AddressRepository;
import com.nhnacademy.byeol23backend.memberset.grade.repository.GradeRepository;
import com.nhnacademy.byeol23backend.memberset.member.domain.Member;
import com.nhnacademy.byeol23backend.memberset.member.domain.Status;
import com.nhnacademy.byeol23backend.memberset.member.dto.MemberCreateRequest;
import com.nhnacademy.byeol23backend.memberset.member.dto.MemberCreateResponse;
import com.nhnacademy.byeol23backend.memberset.member.dto.MemberMyPageResponse;
import com.nhnacademy.byeol23backend.memberset.member.dto.MemberPasswordUpdateRequest;
import com.nhnacademy.byeol23backend.memberset.member.dto.MemberPasswordUpdateResponse;
import com.nhnacademy.byeol23backend.memberset.member.dto.MemberUpdateRequest;
import com.nhnacademy.byeol23backend.memberset.member.dto.MemberUpdateResponse;
import com.nhnacademy.byeol23backend.memberset.member.exception.DuplicateEmailException;
import com.nhnacademy.byeol23backend.memberset.member.exception.DuplicateIdException;
import com.nhnacademy.byeol23backend.memberset.member.exception.DuplicateNicknameException;
import com.nhnacademy.byeol23backend.memberset.member.exception.DuplicatePhoneNumberException;
import com.nhnacademy.byeol23backend.memberset.member.exception.IncorrectPasswordException;
import com.nhnacademy.byeol23backend.memberset.member.exception.MemberNotFoundException;
import com.nhnacademy.byeol23backend.memberset.member.repository.MemberRepository;
import com.nhnacademy.byeol23backend.memberset.member.service.MemberService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RequiredArgsConstructor
@Service
@Slf4j
public class MemberServiceImpl implements MemberService {

	private final MemberRepository memberRepository;
	private final PasswordEncoder passwordEncoder;
	private final GradeRepository gradeRepository;
	private final CartRepository cartRepository;
	private final ApplicationEventPublisher eventPublisher;
	private final AddressRepository addressRepository;

	@Value("${coupon.welcome.policy-id}")
	private Long welcomeCouponPolicyId;

	@Value("${coupon.welcome.coupon-name-template}")
	private String welcomeCouponName;

	@Value("${coupon.welcome.validity-days}")
	private int validityDays;

	/**
	 * 회원을 저장하는 함수
	 * @param request MemberCreateRequest
	 * @return MemberCreateResponse
	 */
	@Override
	@Transactional
	public MemberCreateResponse createMember(MemberCreateRequest request) {

		createValidation(request.loginId(), request.nickname(), request.phoneNumber(), request.email());

		Member newMember = Member.create(
			request.loginId(),
			passwordEncoder.encode(request.loginPassword()),
			request.memberName(),
			request.nickname(),
			request.phoneNumber(),
			request.email(),
			request.birthDate(),
			request.memberRole(),
			request.joinedFrom(),
			gradeRepository.findByGradeName("일반")
		);
		memberRepository.save(newMember);
		cartRepository.save(Cart.create(newMember));
		log.info("멤버 생성을 완료했습니다. {}", newMember.getMemberId());

		//회원가입 성공 시 (save 커밋 성공) 이벤트 발행
		//이벤트 객체는 생일쿠폰 발급 시 사용한 dto 그대로 사용
		eventPublisher.publishEvent(
			new BirthdayCouponIssueRequestDto(
				newMember.getMemberId(),
				welcomeCouponPolicyId,
				welcomeCouponName,
				LocalDate.now().plusDays(validityDays)
			)
		);

		return new MemberCreateResponse();
	}

	/**
	 * 회원을 조회하고 회원 정보를 반환하는 함수
	 * @param memberId Long
	 * @return MemberMyPageResponse
	 */
	@Override
	@Transactional(readOnly = true)
	public MemberMyPageResponse getMember(Long memberId) {

		Member member = findMemberById(memberId);

		Address defaultAddress = addressRepository.findAddressByMemberAndIsDefault(member)
			.orElse(null);

		AddressResponse addressResponse = null;

		if (!Objects.isNull(defaultAddress)) {
			addressResponse = new AddressResponse(
				defaultAddress.getAddressId(),
				defaultAddress.getPostCode(),
				defaultAddress.getAddressInfo(),
				defaultAddress.getAddressDetail(),
				defaultAddress.getAddressExtra(),
				defaultAddress.getAddressAlias(),
				defaultAddress.getIsDefault()
			);
		}

		log.info("회원을 조회하였습니다. {}", member);

		return new MemberMyPageResponse(
			member.getLoginId(),
			member.getMemberName(),
			member.getNickname(),
			member.getPhoneNumber(),
			member.getEmail(),
			member.getBirthDate(),
			member.getCurrentPoint(),
			member.getMemberRole(),
			member.getGrade().getGradeName(),
			addressResponse,
			gradeRepository.getAll()
		);
	}

	/**
	 * 회원 정보를 수정하는 함수
	 * @param memberId Long
	 * @param request MemberUpdateRequest
	 * @return MemberUpdateResponse
	 */
	@Override
	@Transactional
	public MemberUpdateResponse updateMember(Long memberId, MemberUpdateRequest request) {
		Member oldMember = findMemberById(memberId);

		updateValidation(request.nickname(), request.phoneNumber(), request.email(), oldMember.getMemberId());

		oldMember.updateMemberInfo(request);

		log.info("{}멤버를 업데이트 했습니다.", memberId);

		return new MemberUpdateResponse();
	}

	@Override
	@Transactional
	public MemberPasswordUpdateResponse updateMemberPassword(Long memberId, MemberPasswordUpdateRequest request) {
		Member member = findMemberById(memberId);
		if (!passwordEncoder.matches(request.currentPassword(), member.getLoginPassword())) {
			throw new IncorrectPasswordException("비밀번호가 일치하지 않습니다.");
		} else {
			member.updatePassword(passwordEncoder.encode(request.newPassword()));
		}

		log.info("비밀번호 변경을 완료하였습니다.");

		return new MemberPasswordUpdateResponse();
	}

	@Override
	@Transactional
	public void updateMemberPoint(Long memberId, BigDecimal point) {
		Member member = findMemberById(memberId);
		member.updatePoint(point);
	}

	@Override
	public Member getMemberProxy(Long memberId) {
		return memberRepository.getReferenceById(memberId);
	}

	@Override
	public boolean checkIdDuplicated(String loginId) {
		return memberRepository.existsByLoginId(loginId);
	}

	/**
	 * 휴면 상태인 회원을 활성 상태로 변경한다.
	 * @param memberId Long
	 */
	@Override
	public void reactivateMember(Long memberId) {
		Member member = findMemberById(memberId);

		if (member.getStatus() == Status.INACTIVE) {
			member.updateStatus(Status.ACTIVE);
		}
	}

	/**
	 * 회원을 삭제하는 기능을 구현한 함수이다.
	 * 실제로 삭제되지는 않고 상태: 활성 -> 탈퇴 로 변경한다.
	 * @param memberId Long
	 */
	@Override
	@Transactional
	public void deleteMember(Long memberId) {
		Member member = findMemberById(memberId);

		if (member.getStatus() != Status.WITHDRAWN) {
			member.updateStatus(Status.WITHDRAWN);
		}

		log.info("{} 멤버가 탈퇴 처리 되었습니다.", memberId);
	}

	@Override
	@Transactional
	public void deactivateMembersNotLoggedInFor3Months() {
		LocalDateTime threshold = LocalDateTime.now().minusMonths(3);
		memberRepository.deactivateMembersNotLoggedInFor3Months(threshold);
		log.info("마지막 로그인 날짜가 3개월 이전인 회원 휴면 상태로 전환");
	}

	private Member findMemberById(Long memberId) {
		return memberRepository.findById(memberId)
			.orElseThrow(() -> new MemberNotFoundException(memberId + "에 해당하는 멤버를 찾을 수 없습니다."));
	}

	private void createValidation(String loginId, String nickname, String phoneNumber, String email) {
		if (loginId != null && memberRepository.existsByLoginId(loginId)) {
			throw new DuplicateIdException("이미 사용 중인 아이디입니다.");
		}

		if (nickname != null && memberRepository.existsByNickname(nickname)) {
			throw new DuplicateNicknameException("이미 사용 중인 닉네임입니다.");
		}

		if (email != null && memberRepository.existsByEmail(email)) {
			throw new DuplicateEmailException("이미 사용 중인 이메일입니다.");
		}

		if (phoneNumber != null && memberRepository.existsByPhoneNumber(phoneNumber)) {
			throw new DuplicatePhoneNumberException("이미 사용 중인 휴대전화입니다.");
		}
	}

	private void updateValidation(String nickname, String phoneNumber, String email, Long memberId) {

		if (nickname != null &&
			memberRepository.existsByPhoneNumberAndMemberIdNot(nickname, memberId)) {
			throw new DuplicateNicknameException("이미 사용 중인 닉네임입니다.");
		}

		if (email != null &&
			memberRepository.existsByEmailAndMemberIdNot(email, memberId)) {
			throw new DuplicateEmailException("이미 사용 중인 이메일입니다.");
		}
		if (phoneNumber != null &&
			memberRepository.existsByPhoneNumberAndMemberIdNot(phoneNumber, memberId)) {
			throw new DuplicatePhoneNumberException("이미 사용 중인 휴대전화입니다.");
		}
	}

}
