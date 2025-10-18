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

@Entity
@Table(name = "members")
public class Member {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "member_id")
	private Long memberId;

	@Column(name = "login_id", nullable = false, length = 20)
	private String loginId;

	@Column(name = "login_password", nullable = false)
	private String loginPassword;

	@Column(name = "member_name", nullable = false, length = 10)
	private String memberName;

	@Column(name = "nickname", nullable = false, length = 15)
	private String nickname;

	@Column(name = "phone_number", nullable = false, length = 11)
	private String phoneNumber;

	@Column(name = "email", nullable = false, length = 30)
	private String email;

	private LocalDate birthday;

	private LocalDateTime latestLogin;

	private LocalDateTime joinDate;

	@Column(name = "status", nullable = false, length = 10)
	private String status;

	@Column(name = "current_point", nullable = false, precision = 10)
	private BigDecimal currentPoint;

	@Column(name = "member_role", nullable = false, length = 10)
	private String memberRole;

	@Column(name = "joinFrom", nullable = false, length = 10)
	private String joinFrom;

	@Column(name = "refresh_token")
	private String refreshToken;

}
