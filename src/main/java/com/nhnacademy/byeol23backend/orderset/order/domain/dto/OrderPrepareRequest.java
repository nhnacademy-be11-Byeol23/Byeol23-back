package com.nhnacademy.byeol23backend.orderset.order.domain.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import com.nhnacademy.byeol23backend.bookset.book.dto.BookInfoRequest;

public record OrderPrepareRequest(BigDecimal totalBookPrice,
								  BigDecimal actualOrderPrice,
								  String receiver,
								  String postCode,
								  String receiverAddress,
								  String receiverAddressDetail,
								  String receiverAddressExtra,
								  String receiverPhone,
								  LocalDate deliveryArrivedDate,
								  List<BookInfoRequest> bookInfoRequestList) {
}
