package com.nhnacademy.byeol23backend.memberset.member.job;

import com.nhnacademy.byeol23backend.memberset.member.service.MemberService;
import lombok.RequiredArgsConstructor;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class MemberGradeUpdateJob implements Job {
    private final MemberService memberService;

    @Override
    public void execute(JobExecutionContext jobExecutionContext) throws JobExecutionException {
        memberService.updateAllMembersGrade();
    }
}
