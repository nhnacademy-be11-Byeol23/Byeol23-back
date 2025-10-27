package com.nhnacademy.byeol23backend.orderset.payment.service;

import java.io.IOException;
import java.net.http.HttpResponse;

import com.nhnacademy.byeol23backend.orderset.payment.domain.dto.PaymentCancelRequest;
import com.nhnacademy.byeol23backend.orderset.payment.domain.dto.PaymentParamRequest;

public interface PaymentService {
	HttpResponse requestConfirm(PaymentParamRequest paymentParamRequest) throws IOException, InterruptedException;

	HttpResponse requestCancel(PaymentCancelRequest paymentCancelRequest) throws IOException, InterruptedException;
}
