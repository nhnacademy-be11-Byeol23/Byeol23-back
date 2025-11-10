package com.nhnacademy.byeol23backend.pointset.pointhistories.domain;

import com.nhnacademy.byeol23backend.memberset.member.domain.Member;
import com.nhnacademy.byeol23backend.orderset.order.domain.Order;
import com.nhnacademy.byeol23backend.pointset.pointpolicy.domain.PointPolicy;
import com.nhnacademy.byeol23backend.reviewset.review.domain.Review;
import com.nhnacademy.byeol23backend.pointset.pointpolicy.domain.PointPolicy;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "point_histories")
@Getter
@NoArgsConstructor
public class PointHistory {
    @Id
    @Column(name = "point_history_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long pointHistoryId;

    @Column(name = "point_amount", precision = 10, nullable = false)
    private BigDecimal pointAmount;

    @Column(name = "changed_at", nullable = false)
    private LocalDateTime changedAt;

    @JoinColumn(name = "member_id", nullable = false)
    @ManyToOne(fetch = FetchType.LAZY)
    private Member memberId;

	@ManyToOne
	@JoinColumn(name = "point_policy_name")
	private PointPolicy pointPolicy;

	public PointHistory(
		Member member,
		PointPolicy pointPolicy
	) {
		this.memberId = member;
		this.pointPolicy = pointPolicy;
		this.pointAmount = pointPolicy.getSaveAmount();
		this.changedAt = LocalDateTime.now();
	}

	public PointHistory(
		Member member,
		PointPolicy policy,
		BigDecimal additionalAmount
	) {
		this.memberId = member;
		this.pointPolicy = policy;
		this.pointAmount = policy.getSaveAmount().add(additionalAmount);
		this.changedAt = LocalDateTime.now();
	}

}
