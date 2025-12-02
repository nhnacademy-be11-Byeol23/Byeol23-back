package com.nhnacademy.byeol23backend.memberset.member.repository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.nhnacademy.byeol23backend.memberset.member.domain.Member;

public interface MemberRepository extends JpaRepository<Member, Long>, MemberRepositoryCustom {

	//회원 가입
	boolean existsByLoginId(String loginId);

	boolean existsByNickname(String nickname);

	boolean existsByEmail(String email);

	boolean existsByPhoneNumber(String phoneNumber);

	//회원 수정
	boolean existsByPhoneNumberAndMemberIdNot(String phoneNumber, Long memberId);

	boolean existsByEmailAndMemberIdNot(String email, Long memberId);

	Optional<Member> getReferenceByMemberId(Long memberId);

	//특정 생일 회원 조회
	@Query(value = """
		SELECT m.member_id 
		FROM members m 
		WHERE 
		    MONTH(m.birth_date) = :targetMonth 
		    AND m.status = 'ACTIVE'
		""",
		nativeQuery = true)
	List<Long> findMembersByBirthdayMonth(@Param("targetMonth") int targetMonth);

	@Query(
		value = "SELECT COALESCE(SUM(o.total_book_price), 0) " +
			"FROM orders o " +
			"WHERE o.member_id = :memberId " +
			"AND o.ordered_at >= DATE_SUB(NOW(), INTERVAL 3 MONTH) " +
			"AND o.order_status NOT IN ('반품', '결제 취소')",
		nativeQuery = true
	)
	BigDecimal findTotalOrderAmountForLast3Months(@Param("memberId") Long memberId);
}
