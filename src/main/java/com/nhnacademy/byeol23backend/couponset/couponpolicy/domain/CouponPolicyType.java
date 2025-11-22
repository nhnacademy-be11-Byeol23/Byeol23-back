package com.nhnacademy.byeol23backend.couponset.couponpolicy.domain;

import lombok.Getter;

import java.util.Arrays;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Getter
public enum CouponPolicyType {
    BOOK("BOOK", "도서 쿠폰"),
    CATEGORY("CATEGORY", "카테고리 쿠폰"),
    WELCOME("WELCOME", "웰컴 쿠폰");

    public static final Map<String, CouponPolicyType> dbValueMap = Arrays.stream(CouponPolicyType.values()).collect(Collectors.toMap(CouponPolicyType::getDbValue, Function.identity()));
    public static final Map<String, CouponPolicyType> parameterMap = Arrays.stream(CouponPolicyType.values()).collect(Collectors.toMap(CouponPolicyType::getParameter, Function.identity()));

    private String dbValue;
    private String parameter;

    CouponPolicyType(String dbValue, String parameter) {
        this.dbValue = dbValue;
        this.parameter = parameter;
    }

    public static CouponPolicyType fromDbValue(String dbValue) {
        return dbValueMap.get(dbValue);
    }

    public static CouponPolicyType fromParameter(String parameter) {
        return parameterMap.get(parameter);
    }
}
