package com.nhnacademy.byeol23backend.couponset.couponpolicy.domain;


import jakarta.persistence.AttributeConverter;

public class CouponPolicyTypeConverter implements AttributeConverter<CouponPolicyType, String> {

    @Override
    public String convertToDatabaseColumn(CouponPolicyType attribute) {
        return attribute.getDbValue();
    }

    @Override
    public CouponPolicyType convertToEntityAttribute(String dbData) {
        return CouponPolicyType.fromDbValue(dbData);
    }
}
