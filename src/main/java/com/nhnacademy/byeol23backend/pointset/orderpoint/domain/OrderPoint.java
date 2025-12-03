package com.nhnacademy.byeol23backend.pointset.orderpoint.domain;

import com.nhnacademy.byeol23backend.orderset.order.domain.Order;
import com.nhnacademy.byeol23backend.pointset.pointhistories.domain.PointHistory;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "order_point")
@Getter
@NoArgsConstructor
public class OrderPoint {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "order_point_id")
	private Long orderPointId;

	@JoinColumn(name = "order_id", nullable = false)
	@ManyToOne(fetch = FetchType.LAZY)
	private Order order;

	@JoinColumn(name = "point_history_id", nullable = false)
	@ManyToOne(fetch = FetchType.LAZY)
	private PointHistory pointHistory;

	public OrderPoint(Order order, PointHistory pointHistory) {
		this.order = order;
		this.pointHistory = pointHistory;
	}

}

