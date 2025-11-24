package com.nhnacademy.byeol23backend.couponset.couponpolicy.domain;

import lombok.Getter;

@Getter
public enum CouponPolicyType {
    BOOK("BOOK"),
    CATEGORY("CATEGORY"),
    WELCOME("WELCOME");


    private String value;

    CouponPolicyType(String value) {
        this.value = value;
    }

    public static CouponPolicyType fromValue(String value) {
        for (CouponPolicyType type : CouponPolicyType.values()) {
            if (type.value.equalsIgnoreCase(value)) {
                return type;
            }
        }
        // 찾지 못했을 때 예외 처리
        throw new IllegalArgumentException("Invalid CouponPolicyType value: " + value);
    }
}
