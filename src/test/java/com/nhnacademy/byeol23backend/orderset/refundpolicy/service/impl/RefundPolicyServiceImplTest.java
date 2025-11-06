package com.nhnacademy.byeol23backend.orderset.refundpolicy.service.impl;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.*;
import static org.mockito.Mockito.*;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import com.nhnacademy.byeol23backend.orderset.refundpolicy.domain.RefundPolicy;
import com.nhnacademy.byeol23backend.orderset.refundpolicy.domain.dto.RefundPolicyCreateRequest;
import com.nhnacademy.byeol23backend.orderset.refundpolicy.domain.dto.RefundPolicyCreateResponse;
import com.nhnacademy.byeol23backend.orderset.refundpolicy.domain.dto.RefundPolicyInfoResponse;
import com.nhnacademy.byeol23backend.orderset.refundpolicy.exception.RefundPolicyNotFoundException;
import com.nhnacademy.byeol23backend.orderset.refundpolicy.repository.RefundPolicyRepository;

@ExtendWith(MockitoExtension.class) // JUnit 5와 Mockito 통합
class RefundPolicyServiceImplTest {

	@Mock
	private RefundPolicyRepository refundPolicyRepository; // 가짜(Mock) 리포지토리

	@InjectMocks
	private RefundPolicyServiceImpl refundPolicyService; // 테스트 대상 서비스

	private RefundPolicy testPolicy;

	@BeforeEach
	void setUp() {
		// 테스트에서 공통으로 사용할 엔티티 샘플
		testPolicy = RefundPolicy.of(
			"테스트 정책",
			"테스트 조건",
			"테스트 코멘트",
			LocalDateTime.now().minusDays(1)
		);
		// (참고: RefundPolicy.of()가 static factory method가 맞는지 확인 필요)
	}

	@Test
	@DisplayName("getAllRefundPolicies - 정책 목록 페이징 조회")
	void getAllRefundPolicies() {
		// given
		Pageable pageable = PageRequest.of(0, 10);
		// 1. 리포지토리가 반환할 가짜 Page<Entity> 객체 생성
		Page<RefundPolicy> mockPage = new PageImpl<>(List.of(testPolicy), pageable, 1);

		// 2. repository.findAll(pageable)이 호출되면 mockPage를 반환하도록 설정
		given(refundPolicyRepository.findAll(pageable)).willReturn(mockPage);

		// when
		Page<RefundPolicyInfoResponse> resultPage = refundPolicyService.getAllRefundPolicies(pageable);

		// then
		// 3. repository.findAll이 pageable 인자와 함께 1번 호출되었는지 검증
		verify(refundPolicyRepository).findAll(pageable);

		// 4. 반환된 Page<DTO>의 내용이 mockPage와 일치하는지 검증
		assertThat(resultPage).isNotNull();
		assertThat(resultPage.getTotalElements()).isEqualTo(1);
		assertThat(resultPage.getContent()).hasSize(1);
		assertThat(resultPage.getContent().get(0).refundPolicyName()).isEqualTo(testPolicy.getRefundPolicyName());
	}

	@Test
	@DisplayName("createRefundPolicy - 새 환불 정책 생성")
	void createRefundPolicy() {
		// given
		RefundPolicyCreateRequest request = new RefundPolicyCreateRequest(
			"새 정책",
			"새 조건",
			"새 코멘트"
		);

		// 1. repository.save()에 전달될 RefundPolicy 객체를 캡처하기 위한 설정
		ArgumentCaptor<RefundPolicy> policyCaptor = ArgumentCaptor.forClass(RefundPolicy.class);

		// 2. Mock save()가 ID가 없는 (영속화되지 않은) 엔티티를 반환한다고 가정
		//    (save()의 반환값을 사용하므로, 반환 동작을 정의해주는 것이 좋습니다)
		when(refundPolicyRepository.save(any(RefundPolicy.class))).thenAnswer(invocation -> {
			RefundPolicy policy = invocation.getArgument(0);
			// (실제 DB라면 ID가 생성되겠지만, Mock이므로 ID는 null일 것임)
			return policy;
		});

		// when
		RefundPolicyCreateResponse response = refundPolicyService.createRefundPolicy(request);

		// then
		// 3. repository.save()가 1번 호출되었는지, 그리고 그 인자를 캡처
		verify(refundPolicyRepository).save(policyCaptor.capture());

		// 4. 캡처된 RefundPolicy 객체의 값이 request DTO와 일치하는지 검증
		RefundPolicy capturedPolicy = policyCaptor.getValue();
		assertThat(capturedPolicy.getRefundPolicyName()).isEqualTo(request.refundPolicyName());
		assertThat(capturedPolicy.getRefundCondition()).isEqualTo(request.refundCondition());
		assertThat(capturedPolicy.getComment()).isEqualTo(request.comment());
		// 5. changedAt이 현재 시간으로 잘 설정되었는지 검증 (1초 이내 오차)
		assertThat(capturedPolicy.getChangedAt()).isCloseTo(LocalDateTime.now(), within(1, ChronoUnit.SECONDS));

		// 6. 반환된 DTO의 값이 올바른지 검증
		assertThat(response).isNotNull();
		assertThat(response.refundPolicyId()).isNull(); // Mock save()가 ID를 생성하지 않았으므로 null이 맞음
		assertThat(response.refundPolicyName()).isEqualTo(request.refundPolicyName());
	}

	@Test
	@DisplayName("getCurrentRefundPolicy - 현재 정책 조회 (성공)")
	void getCurrentRefundPolicy_Success() {
		// given
		// 1. repository가 testPolicy를 포함한 Optional을 반환하도록 설정
		given(refundPolicyRepository.findFirstByOrderByChangedAtDesc()).willReturn(Optional.of(testPolicy));

		// when
		RefundPolicyInfoResponse response = refundPolicyService.getCurrentRefundPolicy();

		// then
		// 2. repository가 호출되었는지 검증
		verify(refundPolicyRepository).findFirstByOrderByChangedAtDesc();

		// 3. 반환된 DTO가 testPolicy의 값과 일치하는지 검증
		assertThat(response).isNotNull();
		assertThat(response.refundPolicyName()).isEqualTo(testPolicy.getRefundPolicyName());
		assertThat(response.refundCondition()).isEqualTo(testPolicy.getRefundCondition());
		assertThat(response.comment()).isEqualTo(testPolicy.getComment());
		assertThat(response.changedAt()).isEqualTo(testPolicy.getChangedAt());
	}

	@Test
	@DisplayName("getCurrentRefundPolicy - 현재 정책 조회 (실패 - 정책 없음)")
	void getCurrentRefundPolicy_NotFound() {
		// given
		// 1. repository가 빈 Optional을 반환하도록 설정
		given(refundPolicyRepository.findFirstByOrderByChangedAtDesc()).willReturn(Optional.empty());

		// when & then
		// 2. RefundPolicyNotFoundException이 발생하는지 검증
		assertThatThrownBy(() -> refundPolicyService.getCurrentRefundPolicy())
			.isInstanceOf(RefundPolicyNotFoundException.class)
			.hasMessageContaining("반품 정책이 존재하지 않습니다.");

		// 3. repository가 호출되었는지 검증
		verify(refundPolicyRepository).findFirstByOrderByChangedAtDesc();
	}
}