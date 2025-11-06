package com.nhnacademy.byeol23backend.orderset.orderdetail.domain;

import java.math.BigDecimal;

import com.nhnacademy.byeol23backend.bookset.book.domain.Book;
import com.nhnacademy.byeol23backend.orderset.order.domain.Order;
import com.nhnacademy.byeol23backend.orderset.packaging.domain.Packaging;

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
@Getter
@Table(name = "order_details")
@NoArgsConstructor
public class OrderDetail {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "order_detail_id")
	private Long orderDetailId;

	@Column(name = "quantity", nullable = false)
	private Integer quantity;

	@Column(name = "order_price", nullable = false, precision = 10)
	private BigDecimal orderPrice;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "book_id", nullable = false)
	private Book book;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "packaging_id")
	private Packaging packaging;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "order_id", nullable = false)
	private Order order;

	private OrderDetail(Integer quantity, BigDecimal orderPrice,
		Book book, Packaging packaging, Order order) {
		this.quantity = quantity;
		this.orderPrice = orderPrice;
		this.book = book;
		this.packaging = packaging;
		this.order = order;
	}

	public static OrderDetail of(Integer quantity, BigDecimal orderPrice,
		Book book, Packaging packaging, Order order) {
		return new OrderDetail(quantity, orderPrice, book, packaging, order);
	}

}
