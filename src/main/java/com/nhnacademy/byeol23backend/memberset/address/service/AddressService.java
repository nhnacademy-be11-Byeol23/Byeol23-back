package com.nhnacademy.byeol23backend.memberset.address.service;

import java.util.List;

import com.nhnacademy.byeol23backend.memberset.address.dto.AddressInfoResponse;
import com.nhnacademy.byeol23backend.memberset.address.dto.AddressRequest;
import com.nhnacademy.byeol23backend.memberset.address.dto.AddressResponse;

public interface AddressService {
	AddressResponse createOrder(String token, AddressRequest request);

	void updateAddress(AddressRequest request);

	List<AddressInfoResponse> getAddresses(String token);

	void deleteAddress(Long addressId);

	void setDefaultAddress(Long addressId);
}
