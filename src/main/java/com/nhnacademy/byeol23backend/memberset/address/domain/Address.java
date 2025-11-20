package com.nhnacademy.byeol23backend.memberset.address.domain;

import com.nhnacademy.byeol23backend.memberset.address.dto.AddressRequest;
import com.nhnacademy.byeol23backend.memberset.member.domain.Member;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Table(name = "addresses")
@NoArgsConstructor
public class Address {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "address_id")
	private Long addressId;

	@Pattern(regexp = "^\\d{5}$", message = "우편번호는 5자리 숫자여야 합니다.")
	private String postCode;

	@Column(name = "address_info", nullable = false, length = 50)
	private String addressInfo;

	@Column(name = "address_detail", length = 30)
	private String addressDetail;

	@Column(name = "address_extra", length = 30)
	private String addressExtra;

	@Column(name = "address_alias", length = 30)
	private String addressAlias;

	@Setter
	@Column(name = "is_default", nullable = false)
	private Boolean isDefault;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "member_id", nullable = false)
	private Member member;

	private Address(String postCode, String addressInfo, String addressDetail, String addressExtra, String addressAlias,
		Boolean isDefault, Member member) {
		this.postCode = postCode;
		this.addressInfo = addressInfo;
		this.addressDetail = addressDetail;
		this.addressExtra = addressExtra;
		this.addressAlias = addressAlias;
		this.isDefault = isDefault;
		this.member = member;
	}

	public static Address of(String postCode, String addressInfo, String addressDetail, String addressExtra,
		String addressAlias, Boolean isDefault, Member member) {
		return new Address(postCode, addressInfo, addressDetail, addressExtra, addressAlias, isDefault, member);
	}

	public void updateAddress(AddressRequest request) {
		this.postCode = request.postCode();
		this.addressInfo = request.addressInfo();
		this.addressDetail = request.addressDetail();
		this.addressExtra = request.addressExtra();
		this.addressAlias = request.addressAlias();
		this.isDefault = request.isDefault();
	}

}
