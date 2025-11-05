package com.nhnacademy.byeol23backend.memberset.member.repository;

import java.util.List;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import org.springframework.data.jpa.repository.JpaRepository;

import com.nhnacademy.byeol23backend.memberset.member.domain.Member;

public interface MemberRepository extends JpaRepository<Member, Long> {
	List<Member> findAllByGrade_GradeId(Long gradeId);


    //회원 가입
    boolean existsByEmail(@NotBlank(message = "이메일은 필수 입력 값입니다.") @Email(message = "유효한 이메일 형식이 아닙니다.") @Size(max = 30, message = "이메일은 최대 30자까지 입력 가능합니다.") String email);
    boolean existsByPhoneNumber(@NotBlank(message = "전화번호는 필수 입력 값입니다.") @Pattern(regexp = "^\\d{10,11}$", message = "전화번호는 10~11자리 숫자로 입력해야 합니다.") String s);

    //회원 수정
    boolean existsByPhoneNumberAndMemberIdNot(String phoneNumber, Long memberId);
    boolean existsByEmailAndMemberIdNot(String email, Long memberId);

}
