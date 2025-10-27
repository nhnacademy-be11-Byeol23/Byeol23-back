package com.nhnacademy.byeol23backend.memberset.member.domain;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "members")
@Getter
@Setter
public class Member {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "member_id")
	private Long memberId;

	@Column(name = "login_id", nullable = false, length = 20)
	private String loginId;

	@Column(name = "login_password", nullable = false)
	private String loginPassword;

	@Column(name = "member_name", nullable = false, length = 50)
	private String memberName;

	@Column(name = "nickname", nullable = false, length = 50)
	private String nickname;

	@Column(name = "phone_number", nullable = false, length = 11)
	private String phoneNumber;

	@Column(name = "email", nullable = false, length = 50)
	private String email;

	@Column(name = "birth_date", nullable = false)
	private LocalDate birthday;

	@Column(name = "latest_logined_at")
	private LocalDateTime latestLoginAt;

	@Column(name = "joined_at", nullable = false)
	private LocalDateTime joinedAt;

	@Column(name = "status", nullable = false, length = 10)
	private String status;

	@Column(name = "current_point", nullable = false, precision = 10)
	private BigDecimal currentPoint;

	@Column(name = "member_role", nullable = false, length = 10)
	private String memberRole;
	
	@Column(name = "joined_from", nullable = false, length = 50)
	private String joinedFrom;

	@Column(name = "grade_id", nullable = false)
	private Long gradeId;

	@Column(name = "refresh_token")
	private String refreshToken;

	@Override
	public String toString() {
		return "아이디: " + this.loginId
			+ "이름: " + this.memberName
			+ "닉네임: " + this.nickname;
	}

	public Member update(Member member) {
		this.loginId = member.getLoginId();
		this.memberName = member.getMemberName();
		this.nickname = member.getNickname();
		this.phoneNumber = member.getPhoneNumber();
		this.email = member.getEmail();
		this.birthday = member.getBirthday();
		this.status = member.getStatus();
		this.currentPoint = member.getCurrentPoint();
		return member;
	}

	public void updatePassword(String newPassword) {
		this.loginPassword = newPassword;
	}
}
