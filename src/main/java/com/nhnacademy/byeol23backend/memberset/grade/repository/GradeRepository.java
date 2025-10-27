package com.nhnacademy.byeol23backend.memberset.grade.repository;

import java.math.BigDecimal;

import org.springframework.data.jpa.repository.JpaRepository;

import com.nhnacademy.byeol23backend.memberset.grade.domain.Grade;

public interface GradeRepository extends JpaRepository<Grade, Long> {
	Grade findByGradeName(String gradeName);

	void deleteByGradeName(String gradeName);

	/**
	 * 		SELECT * <br>
	 * 		FROM grades <br>
	 * 		WHERE criterion_price &lt; ? <br>
	 * 		ORDER BY criterion_price DESC <br>
	 * 		LIMIT 1; <br>
	 *
	 * 		등급 테이블 업데이트 전, 회원 등급을 일괄적으로 한단계씩 낮추기 위하여 한단계 낮은 등급을 찾아 반환하는 쿼리
	 * @param criterionPrice &lt;BigDecimal&gt;
	 * @return 한단계 낮은 등급
	 */
	Grade findTopByCriterionPriceLessThanOrderByCriterionPriceDesc(BigDecimal criterionPrice);
}
