package com.nhnacademy.byeol23backend.orderset.order.domain.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record OrderInfoResponse(String orderNumber,
								LocalDateTime orderDate,
								String receiver,
								BigDecimal actualOrderPrice,
								String orderStatus) {
}
