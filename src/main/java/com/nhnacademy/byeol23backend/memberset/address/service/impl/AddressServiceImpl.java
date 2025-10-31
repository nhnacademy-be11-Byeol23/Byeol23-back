package com.nhnacademy.byeol23backend.memberset.address.service.impl;

import org.springframework.stereotype.Service;

import com.nhnacademy.byeol23backend.memberset.address.service.AddressService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AddressServiceImpl implements AddressService {
	// private final AddressRepository addressRepository;
	//
	// public Address getAddress(Long memberId) {
	// 	return addressRepository.findById()
	// 		.orElseThrow(() -> new IllegalArgumentException("Address not found with id: " + addressId));
	// }
	//
	// public List<Address> getAddressesByMember(Member member) {
	// 	return addressRepository.findByMember(member);
	// }
	//
	// @Transactional
	// public Address saveAddress(Member member, AddAddressRequest addressRequest) {
	// 	List<Address> existingAddresses = addressRepository.findByMember(member);
	// 	if (existingAddresses.size() >= 5) {
	// 		throw new AddressAlreadyMaxException("주소 최대 저장 개수를 초과하였습니다.");
	// 	}
	// 	boolean isFirstAddress = existingAddresses.isEmpty();
	// 	for (Address address : existingAddresses) {
	// 		if (address.getPostCode().equals(addressRequest.getPostCode())) {
	// 			throw new UnregistableAddress("Address with the same post code already exists");
	// 		}
	// 	}
	//
	// 	Address address = Address.builder()
	// 		.postCode(addressRequest.getPostCode())
	// 		.addressInfo(addressRequest.getAddressInfo())
	// 		.addressDetail(addressRequest.getAddressDetail())
	// 		.addressExtra(addressRequest.getAddressExtra())
	// 		.addressAlias(addressRequest.getAddressAlias())
	// 		.isDefault(isFirstAddress)
	// 		.member(member) // 나중에 회원 엔티티로 설정 필요
	// 		.build();
	// 	return addressRepository.save(address);
	// }
	//
	// @Transactional
	// public void deleteAddress(Member member, Long addressId) {
	// 	Address address = addressRepository.findByAddressIdAndMember(addressId, member)
	// 		.orElseThrow(() -> new IllegalArgumentException("Address not found with id: " + addressId));
	// 	if (Boolean.TRUE.equals(address.getIsDefault())) {
	// 		throw new UserInvalidControlException("Default address cannot be deleted", IllegalArgumentException.class);
	// 	}
	// 	addressRepository.delete(address);
	// }
	//
	// @Transactional
	// public void setDefaultAddress(Member member, Long addressId) {
	// 	Address target = addressRepository.findByAddressIdAndMember(addressId, member)
	// 		.orElseThrow(() -> new IllegalArgumentException("Address not found with id: " + addressId));
	//
	// 	Optional<Address> currentDefaultOpt = addressRepository.findByMemberAndIsDefaultTrue(member);
	// 	if (currentDefaultOpt.isPresent()) {
	// 		Address currentDefault = currentDefaultOpt.get();
	// 		if (!Objects.equals(currentDefault.getAddressId(), target.getAddressId())) {
	// 			currentDefault.setIsDefault(false);
	// 			addressRepository.save(currentDefault);
	// 		}
	// 	}
	// 	target.setIsDefault(true);
	// 	addressRepository.save(target);
	// }
	//
	// @Transactional
	// public Address updateAddress(Member member, Long addressId, UpdateAddressRequest req) {
	// 	Address address = addressRepository.findByAddressIdAndMember(addressId, member)
	// 		.orElseThrow(() -> new IllegalArgumentException("Address not found with id: " + addressId));
	//
	// 	if (req.getPostCode() != null)
	// 		address.setPostCode(req.getPostCode());
	// 	if (req.getAddressInfo() != null)
	// 		address.setAddressInfo(req.getAddressInfo());
	// 	if (req.getAddressDetail() != null)
	// 		address.setAddressDetail(req.getAddressDetail());
	// 	if (req.getAddressExtra() != null)
	// 		address.setAddressExtra(req.getAddressExtra());
	// 	if (req.getAddressAlias() != null)
	// 		address.setAddressAlias(req.getAddressAlias());
	//
	// 	return addressRepository.save(address);
	// }
}
