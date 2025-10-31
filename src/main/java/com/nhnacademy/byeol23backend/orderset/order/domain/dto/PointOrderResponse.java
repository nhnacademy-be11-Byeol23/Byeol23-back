package com.nhnacademy.byeol23backend.orderset.order.domain.dto;

import java.math.BigDecimal;

public record PointOrderResponse(String orderNumber,
								 BigDecimal totalAmount,
								 String method) {

}
