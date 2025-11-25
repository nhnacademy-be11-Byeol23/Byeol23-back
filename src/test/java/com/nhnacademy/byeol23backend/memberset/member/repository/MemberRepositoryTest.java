package com.nhnacademy.byeol23backend.memberset.member.repository;

import com.nhnacademy.byeol23backend.config.QuerydslTestConfig;
import com.nhnacademy.byeol23backend.memberset.grade.domain.Grade;
import com.nhnacademy.byeol23backend.memberset.member.domain.Member;
import com.nhnacademy.byeol23backend.memberset.member.domain.RegistrationSource;
import com.nhnacademy.byeol23backend.memberset.member.domain.Role;
import com.nhnacademy.byeol23backend.memberset.member.domain.Status;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@DataJpaTest
@Import(QuerydslTestConfig.class)
class MemberRepositoryTest {
    @Autowired
    private MemberRepository memberRepository;
    @Autowired
    private EntityManager entityManager;

    @Test
    void deactivateMembersNotLoggedInFor3Months_success() {
        Grade grade = normalGrade();
        entityManager.persist(grade);

        Member dormant = createDormantMember();
        dormant.setGrade(grade);
        entityManager.persist(dormant);

        Member recent = createRecentMember();
        recent.setGrade(grade);
        entityManager.persist(recent);

        entityManager.flush();
        entityManager.clear();

        memberRepository.deactivateMembersNotLoggedInFor3Months(LocalDateTime.now().minusMonths(3));

        entityManager.flush();
        entityManager.clear();

        Member updateDormant = entityManager.find(Member.class, dormant.getMemberId());
        Member updateRecent = entityManager.find(Member.class, recent.getMemberId());

        Assertions.assertEquals(Status.INACTIVE, updateDormant.getStatus());
        Assertions.assertEquals(Status.ACTIVE, updateRecent.getStatus());
    }

    private Member createDormantMember() {
        Member member = new Member();
        member.setLoginId("testuser1");
        member.setLoginPassword("1234");
        member.setMemberName("testuser1");
        member.setNickname("test1");
        member.setPhoneNumber("01012345678");
        member.setEmail("testmember1@gmail.com");
        member.setBirthDate(LocalDate.now().minusDays(10));
        member.setLatestLoginAt(LocalDateTime.now().minusMonths(4));
        member.setJoinedAt(LocalDateTime.now().minusMonths(6));
        member.setStatus(Status.ACTIVE);
        member.setCurrentPoint(BigDecimal.ZERO);
        member.setMemberRole(Role.USER);
        member.setJoinedFrom(RegistrationSource.WEB);

        return member;
    }

    private Member createRecentMember() {
        Member member = new Member();
        member.setLoginId("testuser2");
        member.setLoginPassword("1234");
        member.setMemberName("testuser2");
        member.setNickname("test2");
        member.setPhoneNumber("01098765432");
        member.setEmail("testmember2@gmail.com");
        member.setBirthDate(LocalDate.now().minusDays(15));
        member.setLatestLoginAt(LocalDateTime.now().minusDays(4));
        member.setJoinedAt(LocalDateTime.now().minusMonths(5));
        member.setStatus(Status.ACTIVE);
        member.setCurrentPoint(BigDecimal.ZERO);
        member.setMemberRole(Role.USER);
        member.setJoinedFrom(RegistrationSource.WEB);

        return member;
    }

    private Grade normalGrade() {
        Grade grade = new Grade();
        grade.setGradeName("일반");
        grade.setCriterionPrice(BigDecimal.valueOf(100000L));
        grade.setPointRate(BigDecimal.valueOf(0.01));

        return grade;
    }
}