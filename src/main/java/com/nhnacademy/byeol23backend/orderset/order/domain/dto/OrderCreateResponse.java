package com.nhnacademy.byeol23backend.orderset.order.domain.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

public record OrderCreateResponse(String orderNumber,
								  BigDecimal totalBookPrice,
								  BigDecimal actualOrderPrice,
								  LocalDateTime orderDate,
								  String orderStatus,
								  LocalDate deliveryArrivedAt,
								  String receiver,
								  String postCode,
								  String receiverAddress,
								  String receiverAddressDetail,
								  String receiverPhone) {
}
