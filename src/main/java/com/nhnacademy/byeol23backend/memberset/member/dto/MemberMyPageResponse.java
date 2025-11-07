package com.nhnacademy.byeol23backend.memberset.member.dto;

import com.nhnacademy.byeol23backend.memberset.grade.domain.Grade;
import com.nhnacademy.byeol23backend.memberset.member.domain.Role;

import java.math.BigDecimal;
import java.time.LocalDate;

public record MemberMyPageResponse(
        String loginId,
        String memberName,
        String nickname,
        String phoneNumber,
        String email,
        LocalDate birthDate,
        BigDecimal currentPoint,
        Role memberRole,
        Grade grade
) {
}
