package com.nhnacademy.byeol23backend.orderset.order.domain;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

import com.nhnacademy.byeol23backend.memberset.member.domain.Member;
import com.nhnacademy.byeol23backend.orderset.delivery.domain.DeliveryPolicy;
import com.nhnacademy.byeol23backend.pointset.pointhistories.domain.PointHistory;

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
import com.nhnacademy.byeol23backend.pointset.pointhistories.domain.PointHistory;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "orders")
@Getter
@NoArgsConstructor
public class Order {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "order_id")
	private Long orderId;

	@Column(name = "order_number", nullable = false, length = 18)
	private String orderNumber;

	@Column(name = "order_password")
	private String orderPassword;

	@Column(name = "total_book_price", nullable = false, precision = 10)
	private BigDecimal totalBookPrice;

	@Column(name = "actual_order_price", nullable = false, precision = 10)
	private BigDecimal actualOrderPrice;

	private LocalDateTime orderedAt;

	@Column(name = "order_status", nullable = false, length = 10)
	private String orderStatus;

	private LocalDate deliverySentDate; //배송 시작 날짜

	private LocalDate deliveryDesiredDate; //배송 희망 날짜

	@Column(name = "receiver", nullable = false, length = 10)
	private String receiver;

	@Column(name = "post_code", nullable = false, length = 5)
	private String postCode;

	@Column(name = "receiver_address", nullable = false, length = 30)
	private String receiverAddress;

	@Column(name = "receiver_address_detail", nullable = false, length = 30)
	private String receiverAddressDetail;

	@Column(name = "receiver_address_extra", length = 30)
	private String receiverAddressExtra;

	@Column(name = "receiver_phone", nullable = false, length = 11)
	private String receiverPhone;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "member_id")
	private Member member;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "delivery_policy_id", nullable = false)
	private DeliveryPolicy deliveryPolicy;

	@Setter
	@OneToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "point_history_id", nullable = true)
	private PointHistory pointHistory;

	private Order(String orderNumber, BigDecimal totalBookPrice, BigDecimal actualOrderPrice, LocalDateTime orderedAt,
		String orderStatus, LocalDate deliveryDesiredDate, String receiver, String postCode, String receiverAddress,
		String receiverAddressDetail, String receiverAddressExtra, String receiverPhone,
		DeliveryPolicy deliveryPolicy) {
		this.orderNumber = orderNumber;
		this.totalBookPrice = totalBookPrice;
		this.actualOrderPrice = actualOrderPrice;
		this.orderedAt = orderedAt;
		this.orderStatus = orderStatus;
		this.deliveryDesiredDate = deliveryDesiredDate;
		this.receiver = receiver;
		this.postCode = postCode;
		this.receiverAddress = receiverAddress;
		this.receiverAddressDetail = receiverAddressDetail;
		this.receiverAddressExtra = receiverAddressExtra;
		this.receiverPhone = receiverPhone;
		this.deliveryPolicy = deliveryPolicy;
	}

	public static Order of(String orderNumber, BigDecimal totalBookPrice, BigDecimal actualOrderPrice,
		LocalDate deliveryDesiredDate, String receiver, String postCode, String receiverAddress,
		String receiverAddressDetail, String receiverAddressExtra, String receiverPhone,
		DeliveryPolicy deliveryPolicy) {

		return new Order(orderNumber, totalBookPrice, actualOrderPrice, LocalDateTime.now(),
			"대기", deliveryDesiredDate, receiver, postCode, receiverAddress, receiverAddressDetail, receiverAddressExtra,
			receiverPhone, deliveryPolicy);
	}

	public void updateOrderStatus(String orderStatus) {
		this.orderStatus = orderStatus;
	}

}
