package com.nhnacademy.byeol23backend.couponset.coupon.service.impl;

import com.nhnacademy.byeol23backend.config.CouponIssueRabbitProperties;
import com.nhnacademy.byeol23backend.couponset.coupon.dto.CouponIssueRequestDto;
import com.nhnacademy.byeol23backend.couponset.coupon.repository.CouponRepository;
import com.nhnacademy.byeol23backend.couponset.coupon.service.CouponService;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CouponServiceImpl implements CouponService {
    private final RabbitTemplate rabbitTemplate;
    private final CouponIssueRabbitProperties properties;
    private final CouponRepository couponRepository;

    @Override
    public void sendIssueRequestToMQ(CouponIssueRequestDto request) {
        rabbitTemplate.convertAndSend(
                properties.exchange(),
                properties.routingKey(),
                request
        );
    }

    @Override
    @Transactional
    public void issueCoupon(CouponIssueRequestDto request) {
        int result = couponRepository.issueCouponToAllUsers(request.couponPolicyId(), request.couponName(), request.expiredDate());
        if(result <= 0){
            throw new RuntimeException("쿠폰 발급 실패");
        }
    }
}
