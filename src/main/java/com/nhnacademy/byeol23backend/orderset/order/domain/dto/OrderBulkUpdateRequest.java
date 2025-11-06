package com.nhnacademy.byeol23backend.orderset.order.domain.dto;

import java.util.List;

public record OrderBulkUpdateRequest(List<String> orderNumberLists,
									 String status) {
}
