package com.nhnacademy.byeol23backend.memberset.member.domain;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

import com.nhnacademy.byeol23backend.memberset.grade.domain.Grade;
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
	@Column(name = "login_id", nullable = false, length = 20)
	private String loginId;

	@Setter
	@Column(name = "login_password", nullable = false)
	private String loginPassword;

	@Setter
	@Column(name = "member_name", nullable = false, length = 50)
	private String memberName;

	@Setter
	@Column(name = "nickname", nullable = false, length = 50)
	private String nickname;

	@Setter
	@Column(name = "phone_number", nullable = false, length = 11, unique = true)
	private String phoneNumber;

	@Setter
	@Column(name = "email", nullable = false, length = 50)
	private String email;

	//todo ZoneDate
	@Setter
	@Column(name = "birth_date", nullable = false)
	private LocalDate birthday;

	@Setter
	@Column(name = "latest_logined_at")
	private LocalDateTime latestLoginAt;

	@Column(name = "joined_at", nullable = false)
	private LocalDateTime joinedAt;

	@Setter
	@Column(name = "status", nullable = false, length = 10)
	private String status;

	@Setter
	@Column(name = "current_point", nullable = false, precision = 10)
	private BigDecimal currentPoint;

	@Setter
	@Column(name = "member_role", nullable = false, length = 10)
	private String memberRole;

	@Column(name = "joined_from", nullable = false, length = 50)
	private String joinedFrom;

	@Setter
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "grade_id", nullable = false)
	private Grade grade;

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
}
