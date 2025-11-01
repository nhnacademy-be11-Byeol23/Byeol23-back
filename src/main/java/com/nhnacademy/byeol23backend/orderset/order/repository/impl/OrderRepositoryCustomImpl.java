package com.nhnacademy.byeol23backend.orderset.order.repository.impl;

import static com.nhnacademy.byeol23backend.orderset.order.domain.QOrder.*;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;

import com.nhnacademy.byeol23backend.orderset.order.domain.dto.OrderInfoResponse;
import com.nhnacademy.byeol23backend.orderset.order.domain.dto.OrderSearchCondition;
import com.nhnacademy.byeol23backend.orderset.order.repository.OrderRepositoryCustom;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class OrderRepositoryCustomImpl implements OrderRepositoryCustom {

	private final JPAQueryFactory queryFactory;

	@Override
	public Page<OrderInfoResponse> searchOrders(OrderSearchCondition orderSearchCondition, Pageable pageable) {

		List<OrderInfoResponse> content = queryFactory
			.select(Projections.constructor(OrderInfoResponse.class,
				order.orderNumber,
				order.orderedAt,
				order.receiver,
				order.actualOrderPrice,
				order.orderStatus))
			.from(order)
			.where(
				statusEquals(orderSearchCondition.getStatus()),
				orderNumberContains(orderSearchCondition.getOrderNumber()),
				receiverContains(orderSearchCondition.getReceiver())
			)
			.offset(pageable.getOffset())
			.limit(pageable.getPageSize())
			.fetch();

		JPAQuery<Long> countQuery = queryFactory
			.select(order.count())
			.from(order)
			.where(
				statusEquals(orderSearchCondition.getStatus()),
				orderNumberContains(orderSearchCondition.getOrderNumber()),
				receiverContains(orderSearchCondition.getReceiver())
			);

		Long total = countQuery.fetchOne();
		return new PageImpl<>(content, pageable, (total != null) ? total : 0L);
	}

	private BooleanExpression statusEquals(String status) {
		return StringUtils.hasText(status) ? order.orderStatus.eq(status) : null;
	}

	private BooleanExpression orderNumberContains(String orderNumber) {
		return StringUtils.hasText(orderNumber) ? order.orderNumber.contains(orderNumber) : null;
	}

	private BooleanExpression receiverContains(String receiver) {
		return StringUtils.hasText(receiver) ? order.receiver.contains(receiver) : null;
	}
}