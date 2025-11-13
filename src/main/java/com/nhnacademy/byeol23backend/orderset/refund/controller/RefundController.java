package com.nhnacademy.byeol23backend.orderset.refund.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.nhnacademy.byeol23backend.orderset.refund.domain.dto.RefundRequest;
import com.nhnacademy.byeol23backend.orderset.refund.domain.dto.RefundResponse;
import com.nhnacademy.byeol23backend.orderset.refund.service.RefundService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/refunds")
@RequiredArgsConstructor
public class RefundController {
	private final RefundService refundService;

	/**
	 * 고객의 환불 요청을 접수하고 처리
	 *
	 * @param request 환불 요청 정보 (주문 번호, 환불 사유, 환불 옵션)가 담긴 DTO
	 * @return 환불 처리 결과 (주문 번호, 환불 사유, 환불 옵션, 환불 금액, 환불 요청 시간)가 담긴 DTO와
	 * HTTP 200 OK 상태의 {@link ResponseEntity}
	 */
	@PostMapping
	public ResponseEntity<RefundResponse> refundRequest(@RequestBody RefundRequest request) {
		RefundResponse response = refundService.refundRequest(request);
		return ResponseEntity.ok(response);
	}

}
