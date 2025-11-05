package com.nhnacademy.byeol23backend.memberset.member.service;

import com.nhnacademy.byeol23backend.memberset.member.dto.MemberCreateRequest;
import com.nhnacademy.byeol23backend.memberset.member.dto.MemberCreateResponse;
import com.nhnacademy.byeol23backend.memberset.member.dto.MemberMyPageResponse;
import com.nhnacademy.byeol23backend.memberset.member.dto.MemberUpdateRequest;
import com.nhnacademy.byeol23backend.memberset.member.dto.MemberUpdateResponse;

public interface MemberService {
    MemberCreateResponse createMember(MemberCreateRequest request);
    MemberUpdateResponse updateMember(MemberUpdateRequest request);
    MemberMyPageResponse getMember(Long memberId);
    void deleteMember(Long memberId);
}
