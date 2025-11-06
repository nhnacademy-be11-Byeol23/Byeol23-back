package com.nhnacademy.byeol23backend.memberset.member.domain;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

import com.nhnacademy.byeol23backend.memberset.grade.domain.Grade;
import com.nhnacademy.byeol23backend.memberset.member.dto.MemberPasswordUpdateRequest;
import com.nhnacademy.byeol23backend.memberset.member.dto.MemberUpdateRequest;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "members")
@Getter
@NoArgsConstructor
public class Member {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "member_id")
	private Long memberId;

	@Setter
	@Column(name = "login_id", nullable = false, length = 20, unique = true)
	private String loginId;

	@Setter
	@Column(name = "login_password", nullable = false)
	private String loginPassword;

	@Setter
	@Column(name = "member_name", nullable = false, length = 50)
	private String memberName;

	@Setter
	@Column(name = "nickname", nullable = false, length = 50, unique = true)
	private String nickname;

	@Setter
	@Column(name = "phone_number", nullable = false, length = 11, unique = true)
	private String phoneNumber;

	@Setter
	@Column(name = "email", nullable = false, length = 50, unique = true)
	private String email;

	@Setter
	@Column(name = "birth_date", nullable = false)
	private LocalDate birthDate;

	@Setter
	@Column(name = "latest_logined_at")
	private LocalDateTime latestLoginAt;

	@Column(name = "joined_at", nullable = false)
	private LocalDateTime joinedAt;

	@Setter
	@Enumerated(EnumType.STRING)
	@Column(name = "status", nullable = false, length = 10)
	private Status status;

	@Setter
	@Column(name = "current_point", nullable = false, precision = 10)
	private BigDecimal currentPoint;

	@Setter
	@Enumerated(EnumType.STRING)
	@Column(name = "member_role", nullable = false, length = 10)
	private Role memberRole;

	@Enumerated(EnumType.STRING)
	@Column(name = "joined_from", nullable = false, length = 50)
	private RegistrationSource joinedFrom;

	@Setter
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "grade_id", nullable = false)
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

}
