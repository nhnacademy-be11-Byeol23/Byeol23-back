package com.nhnacademy.byeol23backend.config;


import com.nhnacademy.byeol23backend.couponset.coupon.dto.BirthdayCouponIssueRequestDto;
import com.nhnacademy.byeol23backend.couponset.coupon.service.CouponService;
import com.nhnacademy.byeol23backend.memberset.member.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.List;

@Component
@RequiredArgsConstructor
public class BirthdayCouponJob implements Job {
    private final MemberRepository memberRepository;
    private final CouponService couponService;

    @Value("${coupon.birthday.policy-id}")
    private Long birthDateCouponPolicyId;

    @Value("${coupon.birthday.coupon-name-template}")
    private String couponNameTemplate;

    @Value("${coupon.birthday.validity-days}")
    private int validityDays;

    @Override
    public void execute(JobExecutionContext jobExecutionContext)  {
        // Quartz Job 실행 시점: 매월 1일 새벽 1시 10분 0초

        LocalDate today = LocalDate.now();
        int targetMonth = today.getMonthValue();

        List<Long> birthdayMemberIds = memberRepository.findMembersByBirthdayMonth(targetMonth);

        String couponName = this.couponNameTemplate.replace("{MONTH}", String.valueOf(targetMonth));
        LocalDate expiredDate = today.plusDays(validityDays);

        // 3. MQ 메시지 생성 및 전송 (Producer 역할)
        for (Long memberId : birthdayMemberIds) {
            BirthdayCouponIssueRequestDto request = new BirthdayCouponIssueRequestDto(
                    memberId,         // 개별 발급 대상 userId
                    birthDateCouponPolicyId,
                    couponName,
                    expiredDate
            );
            couponService.sendBirthdayIssueRequestToMQ(request);
        }
    }
}
