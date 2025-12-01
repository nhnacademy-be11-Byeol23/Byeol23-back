package com.nhnacademy.byeol23backend.couponset.coupon.service.impl;

import com.nhnacademy.byeol23backend.config.CouponBirthdayRabbitProperties;
import com.nhnacademy.byeol23backend.config.CouponBulkRabbitProperties;
import com.nhnacademy.byeol23backend.config.CouponIssueRabbitProperties;
import com.nhnacademy.byeol23backend.couponset.coupon.domain.Coupon;
import com.nhnacademy.byeol23backend.couponset.coupon.dto.BirthdayCouponIssueRequestDto;
import com.nhnacademy.byeol23backend.couponset.coupon.dto.CouponIssueRequestDto;
import com.nhnacademy.byeol23backend.couponset.coupon.dto.IssuedCouponInfoResponseDto;
import com.nhnacademy.byeol23backend.couponset.coupon.dto.UsedCouponInfoResponseDto;
import com.nhnacademy.byeol23backend.couponset.coupon.repository.CouponRepository;
import com.nhnacademy.byeol23backend.couponset.coupon.service.CouponService;
import com.nhnacademy.byeol23backend.couponset.couponpolicy.domain.CouponPolicy;
import com.nhnacademy.byeol23backend.utils.JwtParser;
import com.nhnacademy.byeol23backend.utils.MemberUtil;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;


@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class CouponServiceImpl implements CouponService {
    private final RabbitTemplate rabbitTemplate;
    private final CouponIssueRabbitProperties couponIssueRabbitProperties;
    private final CouponBulkRabbitProperties couponBulkRabbitProperties;
    private final CouponBirthdayRabbitProperties couponBirthdayRabbitProperties;
    private final CouponRepository couponRepository;
    private final JwtParser jwtParser;

    @Override
    public void sendIssueRequestToMQ(CouponIssueRequestDto request) {
        rabbitTemplate.convertAndSend(
                couponIssueRabbitProperties.exchange(),
                couponBulkRabbitProperties.routingKey(),
                request
        );
    }

    @Override
    public void sendBirthdayIssueRequestToMQ(BirthdayCouponIssueRequestDto request) {
        rabbitTemplate.convertAndSend(
                couponIssueRabbitProperties.exchange(),
                couponBirthdayRabbitProperties.routingKey(),
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

    @Override
    @Transactional
    public void issueBirthdayCoupon(BirthdayCouponIssueRequestDto request) {
        Long memberId = request.memberId();
        Long policyId = request.birthDateCouponPolicyId();

        boolean alreadyIssued = couponRepository.existsByMember_memberIdAndCouponPolicy_couponPolicyId(memberId, policyId);

        if (alreadyIssued) {
            log.info("[생일 쿠폰 발급] 이미 발급된 쿠폰입니다. MemberID: " + memberId);
            return;
        }

        int issuedCount = couponRepository.issueBirthdayCoupon(request.birthDateCouponPolicyId(), request.couponName(), request.memberId(), request.expiredDate());

        //발급 실패 시 예외 처리도 해야함 (DB 제약조건 오류 등)
        if (issuedCount != 1) {
            // 단일 사용자 발급이 실패하면 RuntimeException을 던져 MQ에 롤백/재처리 신호 필요
            throw new RuntimeException("생일 쿠폰 발급 실패: MemberID=" + memberId);
        }

        log.info("생일 쿠폰 발급 성공, MemberID: " + memberId);
    }

    @Override
    public List<IssuedCouponInfoResponseDto> getIssuedCoupons(Long memberId) {
        List<Coupon> couponList = couponRepository.findByMember_MemberIdAndUsedAtIsNull(memberId);

        LocalDate today = LocalDate.now();

        // 3. Entity -> DTO 변환
        return couponList.stream()
                .map(coupon -> {
                    CouponPolicy policy = coupon.getCouponPolicy();

                    String discountStr;
                    if (policy.getDiscountAmount() != null) {
                        discountStr = policy.getDiscountAmount() + "원";
                    } else {
                        discountStr = policy.getDiscountRate() + "%";
                    }

                    boolean isValid = !today.isAfter(coupon.getExpiredDate());

                    return new IssuedCouponInfoResponseDto(
                            policy.getCouponPolicyId(),
                            coupon.getCouponId(),
                            coupon.getCouponName(),
                            policy.getCouponPolicyType().getValue(),
                            discountStr,
                            policy.getDiscountLimit(),
                            policy.getCriterionPrice(),
                            coupon.getCreatedDate(),
                            coupon.getExpiredDate(),
                            isValid
                    );
                })
                .toList();
    }

    @Override
    public List<UsedCouponInfoResponseDto> getUsedCoupons(Long memberId) {
        List<Coupon> couponList = couponRepository.findByMember_MemberIdAndUsedAtIsNotNull(memberId);

        return couponList.stream()
                .map(coupon -> {
                    CouponPolicy policy = coupon.getCouponPolicy();

                    String discountStr;
                    if (policy.getDiscountAmount() != null) {
                        discountStr = policy.getDiscountAmount() + "원";
                    } else {
                        discountStr = policy.getDiscountRate() + "%";
                    }

                    return new UsedCouponInfoResponseDto(
                            policy.getCouponPolicyId(),
                            coupon.getCouponId(),
                            coupon.getCouponName(),
                            policy.getCouponPolicyType().getValue(),
                            discountStr,
                            policy.getDiscountLimit(),
                            policy.getCriterionPrice(),
                            coupon.getCreatedDate(),
                            coupon.getUsedAt()
                    );
                })
                .toList();
    }

}
