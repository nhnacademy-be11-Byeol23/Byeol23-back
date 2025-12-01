package com.nhnacademy.byeol23backend.memberset.address.service.impl;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.nhnacademy.byeol23backend.memberset.address.domain.Address;
import com.nhnacademy.byeol23backend.memberset.address.dto.AddressInfoResponse;
import com.nhnacademy.byeol23backend.memberset.address.dto.AddressRequest;
import com.nhnacademy.byeol23backend.memberset.address.dto.AddressResponse;
import com.nhnacademy.byeol23backend.memberset.address.exception.AddressCountOverException;
import com.nhnacademy.byeol23backend.memberset.address.exception.AddressNotFoundException;
import com.nhnacademy.byeol23backend.memberset.address.repository.AddressRepository;
import com.nhnacademy.byeol23backend.memberset.address.service.AddressService;
import com.nhnacademy.byeol23backend.memberset.member.domain.Member;
import com.nhnacademy.byeol23backend.memberset.member.exception.MemberNotFoundException;
import com.nhnacademy.byeol23backend.memberset.member.repository.MemberRepository;
import com.nhnacademy.byeol23backend.utils.JwtParser;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class AddressServiceImpl implements AddressService {
	private final AddressRepository addressRepository;
	private final MemberRepository memberRepository;
	private final JwtParser jwtParser;

	@Override
	@Transactional
	public AddressResponse createOrder(Long memberId, AddressRequest request) {
		Member member = getMember(memberId);

		// 주소는 10개 이하로
		if (addressRepository.countAddressesByMember(member) > 10) {
			throw new AddressCountOverException("주소의 개수는 10개를 넘을 수 없습니다.");
		}

		if (request.isDefault()) { //생성 시에 기본 배송지 설정이 true이면
			addressRepository.updateDefaultAddressFalseByMember(member);
		}

		Address address = Address.of(request.postCode(), request.addressInfo(), request.addressDetail(),
			request.addressExtra(), request.addressAlias(), request.isDefault(), member);

		addressRepository.save(address);

		return new AddressResponse(address.getAddressId(), address.getPostCode(), address.getAddressInfo(),
			address.getAddressDetail(), address.getAddressExtra(), address.getAddressAlias(), address.getIsDefault());
	}

	@Override
	@Transactional
	public void updateAddress(AddressRequest request) {
		Address address = addressRepository.findById(request.addressId())
			.orElseThrow(() -> new AddressNotFoundException("해당 아이디의 주소를 찾을 수 없습니다.: " + request.addressId()));

		address.updateAddress(request);
		log.debug("수정 후 주소: {}", address);
	}

	@Override
	public List<AddressInfoResponse> getAddresses(Long memberId) {
		Member member = getMember(memberId);

		List<Address> addresses = addressRepository.findByMemberOrderByIsDefaultDesc(member);

		return addresses.stream()
			.map(address -> new AddressInfoResponse(
				address.getAddressId(),
				address.getPostCode(),
				address.getAddressInfo(),
				address.getAddressDetail(),
				address.getAddressExtra(),
				address.getAddressAlias(),
				address.getIsDefault()
			))
			.toList();
	}

	@Override
	@Transactional
	public void deleteAddress(Long addressId) {
		addressRepository.deleteById(addressId);
	}

	@Override
	@Transactional
	public void setDefaultAddress(Long addressId) {
		Address newDefaultAddress = addressRepository.findById(addressId)
			.orElseThrow(() -> new AddressNotFoundException("해당 주소를 찾을 수 없습니다: " + addressId));

		Member member = newDefaultAddress.getMember();
		if (member == null) {
			throw new MemberNotFoundException("주소에 연결된 회원을 찾을 수 없습니다.: " + member.getMemberId());
		}

		addressRepository.updateDefaultAddressFalseByMember(member);

		newDefaultAddress.setIsDefault(true);
	}

	private Member getMember(Long memberId) {
		return memberRepository.findById(memberId)
			.orElseThrow(() -> new MemberNotFoundException("해당 아이디의 회원을 찾을 수 없습니다.: " + memberId));

	}
}