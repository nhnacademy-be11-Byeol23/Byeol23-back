package com.nhnacademy.byeol23backend.orderset.refund.domain;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import com.nhnacademy.byeol23backend.orderset.order.domain.Order;
import com.nhnacademy.byeol23backend.orderset.refundpolicy.domain.RefundPolicy;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "refunds")
public class Refund {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "refund_id")
	private Long refundId;

	@OneToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "order_id", nullable = false)
	private Order order;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "refund_policy_id", nullable = false)
	private RefundPolicy refundPolicy;

	private LocalDateTime refundedAt;

	@Column(name = "refund_reason", nullable = false, length = 100)
	private String refundReason;

	@Column(name = "refund_quantity", nullable = false)
	private Integer refundQuantity;

	@Column(name = "refund_price", nullable = false, precision = 10)
	private BigDecimal refundPrice;

	@Column(name = "refund_fee", nullable = false, precision = 10)
	private BigDecimal refundFee;

}
