package com.nhnacademy.byeol23backend.couponset.coupon.config;

import com.nhnacademy.byeol23backend.couponset.coupon.service.CouponValidationStrategy;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Configuration
public class StrategyConfig {

    @Bean
    public Map<String, CouponValidationStrategy> validationStrategyMap(
            List<CouponValidationStrategy> strategies) {
        return strategies.stream()
                .collect(Collectors.toMap(
                        strategy -> strategy.getClass().getSimpleName().replace("CouponValidationStrategy", "").toUpperCase(),
                        Function.identity()
                ));
    }
}