package com.nhnacademy.byeol23backend.cartset.cartbook.dto;

import java.util.Map;

public record CartOrderRequest(Map<Long, Integer> cartOrderList) {
}
