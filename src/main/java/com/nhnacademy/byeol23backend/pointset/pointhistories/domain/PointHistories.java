package com.nhnacademy.byeol23backend.pointset.pointhistories.domain;

import com.nhnacademy.byeol23backend.memberset.member.domain.Member;
import com.nhnacademy.byeol23backend.orderset.order.domain.Order;
import com.nhnacademy.byeol23backend.pointset.pointpolicy.domain.PointPolicy;
import com.nhnacademy.byeol23backend.reviewset.review.domain.Review;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "point_histories")
@Getter
@NoArgsConstructor
public class PointHistories {
    @Id
    @Column(name = "point_history_id")
    private Integer pointHistoryId;

    @Column(name = "point_amount", precision = 10, nullable = false)
    private BigDecimal pointAmount;

    private LocalDateTime changedAt;

    @JoinColumn(name = "member_id", nullable = false)
    @ManyToOne(fetch = FetchType.LAZY)
    private Member memberId;

    @JoinColumn(name = "order_id", nullable = false)
    @ManyToOne(fetch = FetchType.LAZY)
    private Order orderId;

    @JoinColumn(name = "review_id", nullable = false)
    @ManyToOne(fetch = FetchType.LAZY)
    private Review reviewId;

    @JoinColumn(name = "point_policy_id", nullable = false)
    @ManyToOne(fetch = FetchType.LAZY)
    private PointPolicy pointPolicyId;
}
