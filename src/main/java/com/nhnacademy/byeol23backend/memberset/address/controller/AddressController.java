package com.nhnacademy.byeol23backend.memberset.address.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.AllArgsConstructor;

@AllArgsConstructor
@RestController
@RequestMapping("api/addresses")
public class AddressController {
	// private final AddressService addressService;
	//
	// @Operation(summary = "주소 추가", description = "사용자 주소를 추가합니다.")
	// @ApiResponses({
	// 	@ApiResponse(responseCode = "200", description = "주소 추가 성공"),
	// 	@ApiResponse(responseCode = "400", description = "잘못된 요청 데이터")
	// })
	// @PostMapping("/add")
	// @RequestMember
	// public ResponseEntity<Void> addAddress(
	// 	@RequestBody @Valid AddAddressRequest addressRequest
	// ) {
	// 	Map<String, Object> claims = RequestMemberContext.getDetails();
	// 	Member member = authService.getMemberById(((Number)claims.get("memberId")).longValue());
	// 	Address address = addressService.saveAddress(member, addressRequest);
	// 	if (address == null) {
	// 		throw new IllegalStateException("Address creation failed");
	// 	}
	// 	return ResponseEntity.ok().build();
	// }
	//
	// @Operation(summary = "주소 목록 조회", description = "사용자의 모든 주소를 조회합니다.")
	// @ApiResponses({
	// 	@ApiResponse(responseCode = "200", description = "주소 목록 조회 성공"),
	// 	@ApiResponse(responseCode = "400", description = "잘못된 요청 데이터")
	// })
	// @GetMapping("/list")
	// @RequestMember
	// public ResponseEntity<List<AddressResponse>> getAddresses() {
	// 	Map<String, Object> claims = RequestMemberContext.getDetails();
	// 	com.nhnacademy.byeol23backend.memberset.address.controller.Member member = null;
	// 	try {
	// 		member = authService.getMemberById(((Number)claims.get("memberId")).longValue());
	// 		if (member == null) {
	// 			throw new IllegalArgumentException("Invalid member ID");
	// 		}
	// 	} catch (IllegalArgumentException e) {
	// 		return ResponseEntity.badRequest().body(new ArrayList<>());
	// 	}
	// 	List<Address> addresses = addressService.getAddressesByMember(member);
	// 	List<AddressResponse> addressResponseList = addresses.stream().map(AddressResponse::new).toList();
	// 	return ResponseEntity.ok(addressResponseList);
	// }
	//
	// // delete address but if it is default address, throw UserInvalidControlException("Default address cannot be deleted", IllegalArgumentException.class)
	// @Operation(summary = "주소 삭제", description = "기본 주소는 삭제할 수 없습니다.")
	// @ApiResponses({
	// 	@ApiResponse(responseCode = "200", description = "주소 삭제 성공"),
	// 	@ApiResponse(responseCode = "400", description = "삭제 실패 또는 잘못된 요청")
	// })
	// @DeleteMapping("/{addressId}")
	// @RequestMember
	// public ResponseEntity<Void> deleteAddress(@PathVariable Long addressId) {
	// 	Member member = resolveMemberFromHeader();
	// 	addressService.deleteAddress(member, addressId);
	// 	return ResponseEntity.ok().build();
	// }
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
	//
	// // update address
	// // UpdateAddressRequest는 각 필드가 nullable임 으로, null이 아닌 필드만 업데이트
	// @Operation(summary = "주소 수정", description = "null이 아닌 필드만 부분 업데이트합니다.")
	// @ApiResponses({
	// 	@ApiResponse(responseCode = "200", description = "주소 수정 성공"),
	// 	@ApiResponse(responseCode = "400", description = "수정 실패 또는 잘못된 요청")
	// })
	// @PatchMapping("/{addressId}")
	// @RequestMember
	// public ResponseEntity<Void> updateAddress(@PathVariable Long addressId,
	// 	@RequestBody @Valid UpdateAddressRequest request) {
	// 	Member member = resolveMemberFromHeader();
	// 	addressService.updateAddress(member, addressId, request);
	// 	return ResponseEntity.ok().build();
	// }
	//
	// private Member resolveMemberFromHeader() {
	// 	Map<String, Object> claims = RequestMemberContext.getDetails();
	// 	if (claims == null || !claims.containsKey("memberId")) {
	// 		throw new IllegalArgumentException("Missing memberId in Request-Member header");
	// 	}
	// 	Object val = claims.get("memberId");
	// 	long memberId;
	// 	if (val instanceof Number num) {
	// 		memberId = num.longValue();
	// 	} else if (val instanceof String s) {
	// 		memberId = Long.parseLong(s);
	// 	} else {
	// 		throw new IllegalArgumentException("Invalid memberId type: " + val.getClass());
	// 	}
	// 	return authService.getMemberById(memberId);
	// }

}
