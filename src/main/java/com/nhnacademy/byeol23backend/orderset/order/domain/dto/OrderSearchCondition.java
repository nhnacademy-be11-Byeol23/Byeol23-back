package com.nhnacademy.byeol23backend.orderset.order.domain.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class OrderSearchCondition {
	private String status;
	private String orderNumber;
	private String receiver;
}