package com.nhnacademy.byeol23backend.memberset.address.dto;

public record AddressResponse(
	Long addressId,
	Integer postCode,
	String addressInfo,
	String addressDetail,
	String addressExtra,
	String addressAlias,
	Boolean isDefault
) {
}
