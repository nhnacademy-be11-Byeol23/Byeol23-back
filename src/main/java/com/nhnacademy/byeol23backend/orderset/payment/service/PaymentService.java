package com.nhnacademy.byeol23backend.orderset.payment.service;

import com.nhnacademy.byeol23backend.orderset.payment.domain.dto.PaymentCancelRequest;
import com.nhnacademy.byeol23backend.orderset.payment.domain.dto.PaymentCancelResponse;
import com.nhnacademy.byeol23backend.orderset.payment.domain.dto.PaymentParamRequest;
import com.nhnacademy.byeol23backend.orderset.payment.domain.dto.PaymentResultResponse;

public interface PaymentService {
	PaymentResultResponse confirmPayment(PaymentParamRequest paymentParamRequest);

	PaymentCancelResponse cancelPayment(PaymentCancelRequest paymentCancelRequest);

	void createPayment(PaymentResultResponse response);
}
