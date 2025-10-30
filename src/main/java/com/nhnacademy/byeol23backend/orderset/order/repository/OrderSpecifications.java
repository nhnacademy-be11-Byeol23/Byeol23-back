package com.nhnacademy.byeol23backend.orderset.order.repository;

import org.springframework.data.jpa.domain.Specification;
import org.springframework.util.StringUtils;

import com.nhnacademy.byeol23backend.orderset.order.domain.Order;

public class OrderSpecifications {

	public static Specification<Order> statusEquals(String status) {
		return (root, query, builder) -> StringUtils.hasText(status) ? builder.equal(root.get("orderStatus"), status) :
			null;
	}

	public static Specification<Order> orderNumberContains(String orderNumber) {
		return (root, query, builder) -> StringUtils.hasText(orderNumber) ?
			builder.like(root.get("orderNumber"), "%" + orderNumber + "%") : null;
	}

	public static Specification<Order> receiverContains(String receiver) {
		return (root, query, builder) -> StringUtils.hasText(receiver) ?
			builder.like(root.get("receiver"), "%" + receiver + "%") : null;
	}

}
