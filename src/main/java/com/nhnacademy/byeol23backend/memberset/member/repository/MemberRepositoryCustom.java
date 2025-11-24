package com.nhnacademy.byeol23backend.memberset.member.repository;

import java.time.LocalDateTime;

public interface MemberRepositoryCustom {
    void deactivateMembersNotLoggedInFor3Months(LocalDateTime threshold);
}
