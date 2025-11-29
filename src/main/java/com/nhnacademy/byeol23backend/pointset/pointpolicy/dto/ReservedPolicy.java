package com.nhnacademy.byeol23backend.pointset.pointpolicy.dto;

public enum ReservedPolicy {
	ORDER("주문"),
	REVIEW("리뷰"),
	REGISTER("회원가입"),
	UNKNOWN("알수없음"),
	CANCEL("취소");

	private final String description;

	ReservedPolicy(String description) {
		this.description = description;
	}

	public String getDescription() {
		return description;
	}
}
