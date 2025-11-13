package com.nhnacademy.byeol23backend.orderset.refund.service;

import com.nhnacademy.byeol23backend.orderset.refund.domain.dto.RefundRequest;
import com.nhnacademy.byeol23backend.orderset.refund.domain.dto.RefundResponse;

public interface RefundService {
	RefundResponse refundRequest(RefundRequest request);
}