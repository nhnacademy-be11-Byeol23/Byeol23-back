package com.nhnacademy.byeol23backend.memberset.member.repository;

import com.nhnacademy.byeol23backend.memberset.member.domain.QMember;
import com.nhnacademy.byeol23backend.memberset.member.domain.Status;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;

@Repository
@RequiredArgsConstructor
public class MemberRepositoryImpl implements MemberRepositoryCustom {
    private final JPAQueryFactory queryFactory;

    @Override
    public void deactivateMembersNotLoggedInFor3Months(LocalDateTime threshold) {
        QMember member = QMember.member;

        queryFactory
                .update(member)
                .set(member.status, Status.INACTIVE)
                .where(member.status.eq(Status.ACTIVE).and(member.latestLoginAt.before(threshold)))
                .execute();
    }
}
