package com.nhnacademy.byeol23backend.memberset.member.domain;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

import com.nhnacademy.byeol23backend.memberset.grade.domain.Grade;
import com.nhnacademy.byeol23backend.memberset.member.dto.MemberUpdateRequest;

import io.swagger.v3.oas.annotations.media.Schema;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * 회원 엔티티
 * 시스템의 회원 정보를 관리하는 도메인 모델
 */
@Entity
@Table(name = "members")
@Getter
@NoArgsConstructor
@Schema(description = "회원 엔티티")
public class Member {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "member_id")
	@Schema(description = "회원 ID", example = "1")
	private Long memberId;

	@Setter
	@Column(name = "login_id", nullable = false, length = 20, unique = true)
	@Schema(description = "로그인 ID", example = "user123")
	private String loginId;

	@Setter
	@Column(name = "login_password", nullable = false)
	@Schema(description = "로그인 비밀번호 (암호화됨)", example = "$2a$10$...")
	private String loginPassword;

	@Setter
	@Column(name = "member_name", nullable = false, length = 50)
	@Schema(description = "회원 이름", example = "홍길동")
	private String memberName;

	@Setter
	@Column(name = "nickname", nullable = false, length = 50, unique = true)
	@Schema(description = "닉네임", example = "길동이")
	private String nickname;

	@Setter
	@Column(name = "phone_number", nullable = false, length = 11, unique = true)
	@Schema(description = "전화번호", example = "01012345678")
	private String phoneNumber;

	@Setter
	@Column(name = "email", nullable = false, length = 50, unique = true)
	@Schema(description = "이메일", example = "wsfa1223@gmail.com")
	private String email;

	@Setter
	@Column(name = "birth_date", nullable = false)
	@Schema(description = "생년월일", example = "1990-12-01")
	private LocalDate birthDate;

	@Setter
	@Column(name = "latest_logined_at")
	@Schema(description = "최근 로그인 일시", example = "2024-01-15T10:30:00")
	private LocalDateTime latestLoginAt;

    @Setter
	@Column(name = "joined_at", nullable = false)
	@Schema(description = "가입 일시", example = "2024-01-01T09:00:00")
	private LocalDateTime joinedAt;

	@Setter
	@Enumerated(EnumType.STRING)
	@Column(name = "status", nullable = false, length = 10)
	@Schema(description = "회원 상태", example = "ACTIVE", allowableValues = {"ACTIVE", "INACTIVE", "WITHDRAWN"})
	private Status status;

	@Setter
	@Column(name = "current_point", nullable = false, precision = 10)
	@Schema(description = "현재 보유 포인트", example = "10000")
	private BigDecimal currentPoint;

	@Setter
	@Enumerated(EnumType.STRING)
	@Column(name = "member_role", nullable = false, length = 10)
	@Schema(description = "회원 역할", example = "MEMBER", allowableValues = {"MEMBER", "ADMIN"})
	private Role memberRole;

    @Setter
	@Enumerated(EnumType.STRING)
	@Column(name = "joined_from", nullable = false, length = 50)
	@Schema(description = "가입 경로", example = "WEB", allowableValues = {"WEB", "MOBILE", "ADMIN"})
	private RegistrationSource joinedFrom;

	@Setter
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "grade_id", nullable = false)
	@Schema(description = "회원 등급")
	private Grade grade;

	public static Member create(String loginId, String loginPassword, String memberName, String nickname,
		String phoneNumber, String email, LocalDate birthDate, Role memberRole, RegistrationSource joinedFrom, Grade grade) {
		return new Member(loginId, loginPassword, memberName, nickname, phoneNumber, email, birthDate,
			LocalDateTime.now(), Status.ACTIVE, BigDecimal.ZERO, memberRole, joinedFrom, grade);
	}

	private Member(String loginId, String loginPassword, String memberName, String nickname,
		String phoneNumber, String email, LocalDate birthDate, LocalDateTime joinedAt, Status status,
		BigDecimal currentPoint, Role memberRole, RegistrationSource joinedFrom, Grade grade){
		this.loginId = loginId;
		this.loginPassword = loginPassword;
		this.memberName = memberName;
		this.nickname = nickname;
		this.phoneNumber = phoneNumber;
		this.email = email;
		this.birthDate = birthDate;
		this.joinedAt = joinedAt;
		this.status = status;
		this.currentPoint = currentPoint;
		this.memberRole = memberRole;
		this.joinedFrom = joinedFrom;
		this.grade = grade;
	}

	public void updateMemberInfo(MemberUpdateRequest request) {
		if(request.memberName() != null) this.memberName = request.memberName();
		if(request.nickname() != null) this.nickname = request.nickname();
		if(request.phoneNumber() != null) this.phoneNumber = request.phoneNumber();
		if(request.email() != null) this.email = request.email();
		if(request.birthDate() != null) this.birthDate = request.birthDate();
	}

	public void updatePassword(String newPassword) {
		this.loginPassword = newPassword;
	}

	public void updateStatus(Status st) {
		this.status = st;
	}

	public void updatePoint(BigDecimal point) {
		this.currentPoint = point;
	}
}
