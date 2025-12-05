package com.nhnacademy.byeol23backend.memberset.member.service.impl;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

import com.nhnacademy.byeol23backend.memberset.member.dto.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.nhnacademy.byeol23backend.cartset.cart.domain.Cart;
import com.nhnacademy.byeol23backend.cartset.cart.repository.CartRepository;
import com.nhnacademy.byeol23backend.couponset.coupon.dto.BirthdayCouponIssueRequestDto;
import com.nhnacademy.byeol23backend.memberset.grade.domain.Grade;
import com.nhnacademy.byeol23backend.memberset.grade.dto.AllGradeResponse;
import com.nhnacademy.byeol23backend.memberset.address.domain.Address;
import com.nhnacademy.byeol23backend.memberset.address.dto.AddressResponse;
import com.nhnacademy.byeol23backend.memberset.address.repository.AddressRepository;
import com.nhnacademy.byeol23backend.memberset.grade.repository.GradeRepository;
import com.nhnacademy.byeol23backend.memberset.grade.service.GradeService;
import com.nhnacademy.byeol23backend.memberset.member.domain.Member;
import com.nhnacademy.byeol23backend.memberset.member.domain.Status;
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

/**
 * 회원 서비스 구현체
 * 회원 관련 비즈니스 로직을 처리하는 서비스 클래스
 */
@RequiredArgsConstructor
@Service
@Slf4j
public class MemberServiceImpl implements MemberService {

	private final MemberRepository memberRepository;
	private final PasswordEncoder passwordEncoder;
	private final GradeRepository gradeRepository;
	private final CartRepository cartRepository;
	private final ApplicationEventPublisher eventPublisher;
	private final GradeService gradeService;
	private final AddressRepository addressRepository;

	@Value("${coupon.welcome.policy-id}")
	private Long welcomeCouponPolicyId;

	@Value("${coupon.welcome.coupon-name-template}")
	private String welcomeCouponName;

	@Value("${coupon.welcome.validity-days}")
	private int validityDays;

	/**
	 * 새로운 회원을 생성합니다.
	 * 회원가입 시 ID, 닉네임, 전화번호, 이메일 중복 검사를 수행하고,
	 * 회원 정보를 저장한 후 장바구니를 생성하고 환영 쿠폰을 발급합니다.
	 *
	 * @param request 회원가입 요청 정보 (로그인 ID, 비밀번호, 이름, 닉네임, 전화번호, 이메일, 생년월일 등)
	 * @return 회원가입 응답
	 * @throws DuplicateIdException ID가 중복된 경우
	 * @throws DuplicateNicknameException 닉네임이 중복된 경우
	 * @throws DuplicatePhoneNumberException 전화번호가 중복된 경우
	 * @throws DuplicateEmailException 이메일이 중복된 경우
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
	 * 회원 ID로 회원 정보를 조회합니다.
	 * 회원의 기본 정보, 포인트, 등급, 기본 주소, 전체 등급 목록을 포함한 마이페이지 정보를 반환합니다.
	 *
	 * @param memberId 조회할 회원 ID
	 * @return 회원 마이페이지 정보 (로그인 ID, 이름, 닉네임, 전화번호, 이메일, 생년월일, 포인트, 역할, 등급, 주소, 등급 목록)
	 * @throws MemberNotFoundException 회원을 찾을 수 없는 경우
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
	 * 회원 정보를 수정합니다.
	 * 닉네임, 전화번호, 이메일의 중복 검사를 수행한 후 회원 정보를 업데이트합니다.
	 *
	 * @param memberId 수정할 회원 ID
	 * @param request 수정할 회원 정보 (이름, 닉네임, 전화번호, 이메일, 생년월일)
	 * @return 회원 정보 수정 응답
	 * @throws MemberNotFoundException 회원을 찾을 수 없는 경우
	 * @throws DuplicateNicknameException 닉네임이 중복된 경우
	 * @throws DuplicatePhoneNumberException 전화번호가 중복된 경우
	 * @throws DuplicateEmailException 이메일이 중복된 경우
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

	/**
	 * 회원 비밀번호를 변경합니다.
	 * 현재 비밀번호를 확인한 후 새 비밀번호로 변경합니다.
	 *
	 * @param memberId 비밀번호를 변경할 회원 ID
	 * @param request 비밀번호 변경 요청 정보 (로그인 ID, 현재 비밀번호, 새 비밀번호)
	 * @return 비밀번호 변경 응답
	 * @throws MemberNotFoundException 회원을 찾을 수 없는 경우
	 * @throws IncorrectPasswordException 현재 비밀번호가 일치하지 않는 경우
	 */
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

	/**
	 * 회원의 포인트를 업데이트합니다.
	 *
	 * @param memberId 포인트를 업데이트할 회원 ID
	 * @param point 변경할 포인트 값
	 * @throws MemberNotFoundException 회원을 찾을 수 없는 경우
	 */
	@Override
	@Transactional
	public void updateMemberPoint(Long memberId, BigDecimal point) {
		Member member = findMemberById(memberId);
		member.updatePoint(point);
	}

	/**
	 * 회원 엔티티를 프록시로 조회합니다.
	 * 지연 로딩을 위한 프록시 객체를 반환합니다.
	 *
	 * @param memberId 조회할 회원 ID
	 * @return 회원 엔티티 프록시
	 */
	@Override
	public Member getMemberProxy(Long memberId) {
		return memberRepository.getReferenceById(memberId);
	}

	/**
	 * 로그인 ID의 중복 여부를 확인합니다.
	 *
	 * @param loginId 확인할 로그인 ID
	 * @return 중복 여부 (true: 중복됨, false: 사용 가능)
	 */
	@Override
	public boolean checkIdDuplicated(String loginId) {
		return memberRepository.existsByLoginId(loginId);
	}

	/**
	 * 휴면 상태인 회원을 활성 상태로 변경합니다.
	 *
	 * @param memberId 재활성화할 회원 ID
	 * @throws MemberNotFoundException 회원을 찾을 수 없는 경우
	 */
	@Override
	public void reactivateMember(Long memberId) {
		Member member = findMemberById(memberId);

		if (member.getStatus() == Status.INACTIVE) {
			member.updateStatus(Status.ACTIVE);
		}
	}

	/**
	 * 회원을 탈퇴 처리합니다 (Soft Delete).
	 * 실제로 데이터를 삭제하지 않고 회원 상태를 WITHDRAWN으로 변경합니다.
	 *
	 * @param memberId 탈퇴 처리할 회원 ID
	 * @throws MemberNotFoundException 회원을 찾을 수 없는 경우
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

	/**
	 * 3개월 이상 로그인하지 않은 회원을 휴면 상태로 전환합니다.
	 * 스케줄러에 의해 주기적으로 실행됩니다.
	 */
	@Override
	@Transactional
	public void deactivateMembersNotLoggedInFor3Months() {
		LocalDateTime threshold = LocalDateTime.now().minusMonths(3);
		memberRepository.deactivateMembersNotLoggedInFor3Months(threshold);
		log.info("마지막 로그인 날짜가 3개월 이전인 회원 휴면 상태로 전환");
	}

	@Override
	@Transactional
	public void updateAllMembersGrade() {
		log.info("전체 회원 등급 업데이트 시작");
		LocalDateTime threeMonthsAgo = LocalDateTime.now().minusMonths(3);

		List<Member> activeMembers = memberRepository.findAll().stream()
			.filter(member -> member.getStatus() == Status.ACTIVE)
			.toList();

		for (Member member : activeMembers) {
			try {
				BigDecimal totalBookPriceSum =
					memberRepository.findTotalOrderAmountForLast3Months(member.getMemberId());
				log.info("회원 ID: {}, 순수 결제 금액: {}", member.getMemberId(), totalBookPriceSum);
				Grade prevGrade = member.getGrade();
				Grade newGrade = determineGradeByAmount(totalBookPriceSum);

				if (!prevGrade.getGradeId().equals(newGrade.getGradeId())) {
					member.setGrade(newGrade);
					log.info("회원 ID: {}, 등급 변경: {} -> {} (최근 3개월 도서금액 합계: {}원)",
						member.getMemberId(),
						prevGrade.getGradeName(),
						newGrade.getGradeName(),
						totalBookPriceSum);
				}
			} catch (Exception e) {
				log.error("회원 ID: {} 등급 업데이트 실패", member.getMemberId(), e);
			}
		}

	}

	private Grade determineGradeByAmount(BigDecimal pureOrderAmount) {
		List<AllGradeResponse> grades = gradeService.getAllGrades();

		List<AllGradeResponse> sortedGrades = grades.stream()
			.sorted((g1, g2) -> g2.criterionPrice().compareTo(g1.criterionPrice()))
			.toList();

		for (AllGradeResponse gradeResponse : sortedGrades) {
			if (pureOrderAmount.compareTo(gradeResponse.criterionPrice()) >= 0) {
				return gradeRepository.findByGradeName(gradeResponse.gradeName());
			}
		}
		return gradeRepository.findByGradeName("일반");
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

	/**
	 * 회원 정보(ID, 닉네임, 전화번호, 이메일)의 중복 여부를 확인합니다.
	 * 여러 필드의 중복 여부를 한 번에 확인할 수 있습니다.
	 *
	 * @param request 중복 확인 요청 정보 (로그인 ID, 닉네임, 전화번호, 이메일)
	 * @return 각 필드별 중복 여부 (ID, 닉네임, 이메일, 전화번호)
	 */
	@Override
	public ValueDuplicatedResponse checkInfoDuplicated(ValueDuplicatedRequest request) {
		String nickname = request.nickname();
		String email = request.email();
		String phoneNumber = request.phoneNumber();

		boolean isDuplicatedNickname =
			nickname != null && !nickname.isBlank()
				&& memberRepository.existsByNickname(nickname);

		boolean isDuplicatedEmail =
			email != null && !email.isBlank()
				&& memberRepository.existsByEmail(email);

		boolean isDuplicatedPhoneNumber =
			phoneNumber != null && !phoneNumber.isBlank()
				&& memberRepository.existsByPhoneNumber(phoneNumber);

		return new ValueDuplicatedResponse(isDuplicatedNickname, isDuplicatedEmail, isDuplicatedPhoneNumber);
	}

}
