package com.nhnacademy.byeol23backend.memberset.address.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.nhnacademy.byeol23backend.memberset.address.dto.AddressInfoResponse;
import com.nhnacademy.byeol23backend.memberset.address.dto.AddressRequest;
import com.nhnacademy.byeol23backend.memberset.address.dto.AddressResponse;
import com.nhnacademy.byeol23backend.memberset.address.service.AddressService;
import com.nhnacademy.byeol23backend.utils.MemberUtil;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.AllArgsConstructor;

@AllArgsConstructor
@RestController
@RequestMapping("/api/addresses")
public class AddressController {
	private final AddressService addressService;

	@Operation(summary = "주소 추가", description = "사용자 주소를 추가합니다.")
	@ApiResponses({
		@ApiResponse(responseCode = "200", description = "주소 추가 성공"),
		@ApiResponse(responseCode = "400", description = "잘못된 요청 데이터")
	})
	@PostMapping
	public ResponseEntity<AddressResponse> createAddress(
		@RequestBody AddressRequest request) {
		Long memberId = MemberUtil.getMemberId();
		AddressResponse response = addressService.createOrder(memberId, request);
		return ResponseEntity.status(HttpStatus.CREATED).body(response);
	}

	@Operation(summary = "주소 수정", description = "요청 바디로 들어온 값으로 수정합니다")
	@ApiResponses({
		@ApiResponse(responseCode = "200", description = "주소 수정 성공"),
		@ApiResponse(responseCode = "400", description = "수정 실패 또는 잘못된 요청")
	})
	@PutMapping
	public ResponseEntity<Void> updateAddress(@RequestBody AddressRequest request) {
		addressService.updateAddress(request);
		return ResponseEntity.status(HttpStatus.OK).build();
	}

	@Operation(summary = "주소 목록 조회", description = "사용자의 모든 주소를 조회합니다.")
	@ApiResponses({
		@ApiResponse(responseCode = "200", description = "주소 목록 조회 성공"),
		@ApiResponse(responseCode = "400", description = "잘못된 요청 데이터")
	})
	@GetMapping
	public ResponseEntity<List<AddressInfoResponse>> getAddresses() {
		Long memberId = MemberUtil.getMemberId();
		List<AddressInfoResponse> responses = addressService.getAddresses(memberId);
		return ResponseEntity.status(HttpStatus.OK).body(responses);
	}

	@Operation(summary = "주소 삭제", description = "기본 주소는 삭제할 수 없습니다.")
	@ApiResponses({
		@ApiResponse(responseCode = "200", description = "주소 삭제 성공"),
		@ApiResponse(responseCode = "400", description = "삭제 실패 또는 잘못된 요청")
	})
	@DeleteMapping("/{address-id}")
	public ResponseEntity<Void> deleteAddress(@PathVariable(name = "address-id") Long addressId) {
		addressService.deleteAddress(addressId);
		return ResponseEntity.noContent().build();
	}

	@Operation(summary = "기본 배송지 설정", description = "기본 배송지로 설정하느 메서드입니다. 기존 기본 배송지는 일반 배송지로 변경됩니다.")
	@ApiResponses({
		@ApiResponse(responseCode = "200", description = "기본 배송지 변경 성공"),
		@ApiResponse(responseCode = "400", description = "기본 배송지 변경 실패 또는 잘못된 요청")
	})
	@PostMapping("/{address-id}")
	public ResponseEntity<Void> setDefaultAddress(@PathVariable(name = "address-id") Long addressId) {
		addressService.setDefaultAddress(addressId);
		return ResponseEntity.ok().build();
	}

	//
	// // set default address
	// @Operation(summary = "기본 주소 설정", description = "해당 주소를 기본 주소로 설정합니다.")
	// @ApiResponses({
	// 	@ApiResponse(responseCode = "200", description = "기본 주소 설정 성공"),
	// 	@ApiResponse(responseCode = "400", description = "설정 실패 또는 잘못된 요청")
	// })
	// @PatchMapping("/{addressId}/default")
	// @RequestMember
	// public ResponseEntity<Void> setDefaultAddress(@PathVariable Long addressId) {
	// 	Member member = resolveMemberFromHeader();
	// 	addressService.setDefaultAddress(member, addressId);
	// 	return ResponseEntity.ok().build();
	// }

}
