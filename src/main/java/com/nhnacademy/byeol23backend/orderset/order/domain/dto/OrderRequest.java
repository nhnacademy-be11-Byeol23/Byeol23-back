package com.nhnacademy.byeol23backend.orderset.order.domain.dto;

import java.util.Map;

public record OrderRequest(Map<Long, Integer> orderList,
						   Boolean isCartCheckout) {
}
