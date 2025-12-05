package com.nhnacademy.byeol23backend.memberset.member.dto;

public record ValueDuplicatedResponse(
        boolean isDuplicatedId,
        boolean isDuplicatedNickname,
        boolean isDuplicatedEmail,
        boolean isDuplicatedPhoneNumber
) {
}
