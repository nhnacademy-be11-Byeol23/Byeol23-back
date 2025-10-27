package com.nhnacademy.byeol23backend.memberset.address.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.nhnacademy.byeol23backend.memberset.address.domain.Address;
import com.nhnacademy.byeol23backend.memberset.member.domain.Member;

public interface AddressRepository extends JpaRepository<Address, Long> {

	List<Address> findByMember(Member member);

	Optional<Address> findByAddressIdAndMember(Long addressId, Member member);

	Optional<Address> findByMemberAndIsDefaultTrue(Member member);
}