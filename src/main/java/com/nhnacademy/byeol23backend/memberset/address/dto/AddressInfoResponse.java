package com.nhnacademy.byeol23backend.memberset.address.dto;

public record AddressInfoResponse(Long addressId,
								  String postCode,
								  String addressInfo,
								  String addressDetail,
								  String addressExtra,
								  String addressAlias,
								  Boolean isDefault) {
}

