package com.nhnacademy.byeol23backend.memberset.address.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.nhnacademy.byeol23backend.memberset.address.domain.Address;
import com.nhnacademy.byeol23backend.memberset.member.domain.Member;

public interface AddressRepository extends JpaRepository<Address, Long> {

	List<Address> findByMemberOrderByIsDefaultDesc(Member member);

	@Query("SELECT COUNT(a) FROM Address a WHERE a.member = :member")
	int countAddressesByMember(@Param("member") Member member);

	@Modifying
	@Query("UPDATE Address SET isDefault = false WHERE member = :member AND isDefault = true")
	int updateDefaultAddressFalseByMember(@Param("member") Member member);

}