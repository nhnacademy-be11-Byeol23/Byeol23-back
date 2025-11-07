package com.nhnacademy.byeol23backend.memberset.member.service;

import com.nhnacademy.byeol23backend.memberset.member.dto.*;

public interface MemberService {
    MemberCreateResponse createMember(MemberCreateRequest request);
    MemberMyPageResponse getMember(Long memberId);
    MemberUpdateResponse updateMember(Long memberId, MemberUpdateRequest request);
    MemberPasswordUpdateResponse updateMemberPassword(Long memberId, MemberPasswordUpdateRequest request);
    void reactivateMember(Long memberId);
    void deleteMember(Long memberId);
}
