package com.nhnacademy.byeol23backend.memberset.grade.service.impl;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.stereotype.Service;

import com.nhnacademy.byeol23backend.memberset.grade.dto.AllGradeResponse;
import com.nhnacademy.byeol23backend.memberset.grade.dto.GradeDto;
import com.nhnacademy.byeol23backend.memberset.grade.repository.GradeRepository;
import com.nhnacademy.byeol23backend.memberset.grade.service.GradeService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class GradeServiceImpl implements GradeService {

	private final GradeRepository gradeRepository;

	@Override
	public void createGrade(String gradeName, BigDecimal criterionPrice, BigDecimal pointRate) {
		
	}

	@Override
	public void updateGrade(String gradeName, BigDecimal criterionPrice, BigDecimal pointRate) {

	}

	@Override
	public GradeDto getGrade(String gradeName) {
		return null;
	}

	@Override
	public void deleteGrade(String gradeName) {

	}

	@Override
	public List<AllGradeResponse> getAllGrades(){
		return gradeRepository.getAll();
	}


	// private final GradeRepository gradeRepository;
	// private final MemberRepository memberRepository;
	//
	// /**
	//  * 등급을 추가하는 함수
	//  * @param gradeName &lt;String&gt;
	//  * @param criterionPrice int
	//  */
	// @Override
	// public void createGrade(String gradeName, BigDecimal criterionPrice, BigDecimal pointRate) {
	// 	Grade newGrade = new Grade(gradeName, criterionPrice, pointRate);
	// 	gradeRepository.save(newGrade);
	// 	log.info("{} 등급을 추가하였습니다.", newGrade.getGradeName());
	// }
	//
	// @Override
	// @Transactional
	// public void updateGrade(String gradeName, BigDecimal criterionPrice, BigDecimal pointRate) {
	// 	Grade oldGrade = gradeRepository.findByGradeName(gradeName);
	// 	oldGrade.update(gradeName, criterionPrice, pointRate);
	// 	log.info("{} 등급을 {}으로 기준 가격을 {} -> {} 로 업데이트 했습니다.",
	// 		oldGrade.getGradeName(), gradeName,
	// 		oldGrade.getCriterionPrice(), criterionPrice);
	// }
	//
	// @Override
	// public GradeDto getGrade(String gradeName) {
	// 	Grade target = gradeRepository.findByGradeName(gradeName);
	// 	return new GradeDto(target.getGradeName(), target.getCriterionPrice());
	// }
	//
	// /**
	//  * 등급 이름으로 등급을 삭제하는 함수
	//  * @param gradeName &lt;String&gt;
	//  */
	// @Override
	// public void deleteGrade(String gradeName) {
	// 	gradeRepository.deleteByGradeName(gradeName);
	// }
	//
	// @Transactional
	// public void decreaseGrade(List<Member> memberList, BigDecimal criterion) {
	// 	Long target =
	// 		gradeRepository.findTopByCriterionPriceLessThanOrderByCriterionPriceDesc(criterion).getGradeId();
	// 	for (Member member : memberList) {
	// 		member.setGradeId(target);
	// 	}
	// }
	//
	// @Transactional
	// public void increaseGrade(List<Member> memberList, Grade grade) {
	// 	Long target = grade.getGradeId();
	// 	for (Member member : memberList) {
	//
	// 	}
	// }
	//
	// @Transactional
	// protected void beforeUpdateGrade(Long gradeId) {
	// 	List<Member> memberList = memberRepository.findAllByGradeId(gradeId);
	// 	for (Member member : memberList) {
	// 		if (member.getGradeId() != 1L)
	// 			member.setGradeId(gradeId - 1L);
	// 	}
	// }
	//
	// @Transactional
	// protected void afterUpdateGrade() {
	// 	List<Member> memberList = memberRepository.findAll();
	// 	for (Member member : memberList) {
	// 		member.setGradeIdBy();
	// 	}
	// }
}
