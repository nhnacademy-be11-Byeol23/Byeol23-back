package com.nhnacademy.byeol23backend.memberset.member.controller;

import com.nhnacademy.byeol23backend.memberset.member.domain.Member;
import com.nhnacademy.byeol23backend.memberset.member.repository.MemberRepository;
import com.nhnacademy.byeol23backend.memberset.member.service.MemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RestController("/members")
public class MemberController {
    private final MemberService memberService;

    @PostMapping
    public Member createMember(Member member) {
        return memberService.createMember(member);
    }

    @GetMapping("/{memberId}")
    public Member getMember(Long memberId) {
        return memberService.getMember(memberId);
    }

    @DeleteMapping("/{memberId}")
    public void deleteMember(Long memberId) {
        memberService.deleteMember(memberId);
    }

    @PutMapping("/{memberId}")
    public void updateMember(Member member) {
        memberService.updateMember(member);
    }

}
