package com.nhnacademy.byeol23backend.couponset.couponpolicy.domain;


import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter
public class CouponPolicyTypeConverter implements AttributeConverter<CouponPolicyType, String> {

    @Override
    public String convertToDatabaseColumn(CouponPolicyType attribute) {
        return attribute.getValue();
    }

    @Override
    public CouponPolicyType convertToEntityAttribute(String dbData) {
        return CouponPolicyType.fromValue(dbData);
    }
}
