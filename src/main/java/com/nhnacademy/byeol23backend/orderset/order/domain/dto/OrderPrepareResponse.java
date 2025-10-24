package com.nhnacademy.byeol23backend.orderset.order.domain.dto;

import java.math.BigDecimal;

public record OrderPrepareResponse(String orderNumber,
								   BigDecimal actualOrderPrice,
								   String receiver) {

}
