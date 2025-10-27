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
	public AddressResponse(Address address) {
		this(
			address.getAddressId(),
			address.getPostCode(),
			address.getAddressInfo(),
			address.getAddressDetail(),
			address.getAddressExtra(),
			address.getAddressAlias(),
			address.getIsDefault()
		);
	}
}
