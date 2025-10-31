package com.nhnacademy.byeol23backend.memberset.address.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class AddAddressRequest {
	@NotNull
	@Min(1000)
	@Max(63999)
	private Integer postCode;

	@NotBlank
	@Size(max = 50)
	private String addressInfo;

	@Size(max = 30)
	private String addressDetail;

	@Size(max = 30)
	private String addressExtra;

	@Size(max = 30)
	private String addressAlias;
}
