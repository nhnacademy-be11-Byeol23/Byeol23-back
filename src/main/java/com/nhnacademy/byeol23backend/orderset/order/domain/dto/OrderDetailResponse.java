package com.nhnacademy.byeol23backend.orderset.order.domain.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import com.nhnacademy.byeol23backend.bookset.book.domain.dto.BookOrderInfoResponse;

public record OrderDetailResponse(String orderNumber,
								  LocalDateTime orderDate,
								  String orderStatus,
								  BigDecimal actualOrderPrice,
								  String receiver,
								  String receiverPhone,
								  String receiverAddress,
								  String receiverAddressDetail,
								  String postCode,
								  List<BookOrderInfoResponse> items
) {
}
