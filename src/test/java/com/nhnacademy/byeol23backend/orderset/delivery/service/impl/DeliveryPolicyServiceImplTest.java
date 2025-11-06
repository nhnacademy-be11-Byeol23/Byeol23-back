package com.nhnacademy.byeol23backend.orderset.delivery.service.impl;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.BDDMockito.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Optional;

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

import com.nhnacademy.byeol23backend.orderset.delivery.domain.DeliveryPolicy;
import com.nhnacademy.byeol23backend.orderset.delivery.domain.dto.DeliveryPolicyCreateRequest;
import com.nhnacademy.byeol23backend.orderset.delivery.domain.dto.DeliveryPolicyCreateResponse;
import com.nhnacademy.byeol23backend.orderset.delivery.domain.dto.DeliveryPolicyInfoResponse;
import com.nhnacademy.byeol23backend.orderset.delivery.exception.DeliveryPolicyNotFoundException;
import com.nhnacademy.byeol23backend.orderset.delivery.repository.DeliveryPolicyRepository;

@ExtendWith(MockitoExtension.class) // 1. Mockito를 JUnit 5와 통합
class DeliveryPolicyServiceImplTest {

	@Mock
	private DeliveryPolicyRepository deliveryPolicyRepository; // 2. 의존성을 @Mock으로 선언

	@InjectMocks
	private DeliveryPolicyServiceImpl deliveryPolicyService; // 3. @Mock을 주입할 테스트 대상

	@Test
	@DisplayName("배송 정책 목록 페이징 조회")
	void getDeliveryPolicies() {
		// given
		Pageable pageable = PageRequest.of(0, 10);
		DeliveryPolicy policy = new DeliveryPolicy(BigDecimal.valueOf(50000), BigDecimal.valueOf(3000),
			LocalDateTime.now());
		Page<DeliveryPolicy> mockPage = new PageImpl<>(List.of(policy), pageable, 1);

		// 1. repository.findAll(pageable)이 mockPage를 반환하도록 설정
		given(deliveryPolicyRepository.findAll(pageable)).willReturn(mockPage);

		// when
		Page<DeliveryPolicyInfoResponse> resultPage = deliveryPolicyService.getDeliveryPolicies(pageable);

		// then
		// 2. repository.findAll이 pageable 인자와 함께 호출되었는지 검증
		verify(deliveryPolicyRepository).findAll(pageable);

		// 3. 결과 Page 객체의 내용이 올바르게 매핑되었는지 검증
		assertThat(resultPage).isNotNull();
		assertThat(resultPage.getTotalElements()).isEqualTo(1);
		assertThat(resultPage.getContent()).hasSize(1);
		assertThat(resultPage.getContent().get(0).deliveryFee()).isEqualTo(policy.getDeliveryFee());
		assertThat(resultPage.getContent().get(0).freeDeliveryCondition()).isEqualTo(policy.getFreeDeliveryCondition());
	}

	@Test
	@DisplayName("새 배송 정책 생성")
	void createDeliveryPolicy() {
		// given
		DeliveryPolicyCreateRequest request = new DeliveryPolicyCreateRequest(
			BigDecimal.valueOf(60000),
			BigDecimal.valueOf(2500)
		);

		// 1. repository.save()가 호출될 때 전달되는 DeliveryPolicy 객체를 캡처하기 위한 설정
		ArgumentCaptor<DeliveryPolicy> policyCaptor = ArgumentCaptor.forClass(DeliveryPolicy.class);

		// when
		DeliveryPolicyCreateResponse response = deliveryPolicyService.createDeliveryPolicy(request);

		// then
		// 2. repository.save()가 1번 호출되었는지, 그리고 그 인자를 캡처
		verify(deliveryPolicyRepository).save(policyCaptor.capture());

		// 3. 캡처된 DeliveryPolicy 객체의 값이 request DTO와 일치하는지 검증
		DeliveryPolicy capturedPolicy = policyCaptor.getValue();
		assertThat(capturedPolicy.getDeliveryFee()).isEqualTo(request.deliveryFee());
		assertThat(capturedPolicy.getFreeDeliveryCondition()).isEqualTo(request.freeDeliveryCondition());
		// 4. changedAt이 현재 시간으로 잘 설정되었는지 검증 (1초 이내 오차)
		assertThat(capturedPolicy.getChangedAt()).isCloseTo(LocalDateTime.now(), within(1, ChronoUnit.SECONDS));

		// 5. 반환된 DTO의 값이 (ID를 제외하고) 올바른지 검증
		// (ID는 @Mock save()가 반환하지 않으므로 null이 정상)
		assertThat(response).isNotNull();
		assertThat(response.deliveryPolicyId()).isNull(); // Mock save()는 ID를 생성하지 않음
		assertThat(response.deliveryFee()).isEqualTo(request.deliveryFee());
	}

	@Test
	@DisplayName("현재 배송 정책 조회 (성공)")
	void getCurrentDeliveryPolicy_Success() {
		// given
		DeliveryPolicy mockPolicy = new DeliveryPolicy(
			BigDecimal.valueOf(50000),
			BigDecimal.valueOf(3000),
			LocalDateTime.now()
		);
		// 1. repository가 mockPolicy를 포함한 Optional을 반환하도록 설정
		given(deliveryPolicyRepository.findFirstByOrderByChangedAtDesc()).willReturn(Optional.of(mockPolicy));

		// when
		DeliveryPolicyInfoResponse response = deliveryPolicyService.getCurrentDeliveryPolicy();

		// then
		// 2. repository가 호출되었는지 검증
		verify(deliveryPolicyRepository).findFirstByOrderByChangedAtDesc();

		// 3. 반환된 DTO가 mockPolicy의 값과 일치하는지 검증 (서비스의 of() 메서드 기준)
		assertThat(response).isNotNull();
		assertThat(response.deliveryFee()).isEqualTo(mockPolicy.getDeliveryFee());
		assertThat(response.freeDeliveryCondition()).isEqualTo(mockPolicy.getFreeDeliveryCondition());
	}

	@Test
	@DisplayName("현재 배송 정책 조회 (실패 - 정책 없음)")
	void getCurrentDeliveryPolicy_NotFound() {
		// given
		// 1. repository가 빈 Optional을 반환하도록 설정
		given(deliveryPolicyRepository.findFirstByOrderByChangedAtDesc()).willReturn(Optional.empty());

		// when & then
		// 2. DeliveryPolicyNotFoundException이 발생하는지 검증
		assertThatThrownBy(() -> deliveryPolicyService.getCurrentDeliveryPolicy())
			.isInstanceOf(DeliveryPolicyNotFoundException.class)
			.hasMessageContaining("정책 아이디를 찾을 수 없습니다.");

		// 3. repository가 호출되었는지 검증
		verify(deliveryPolicyRepository).findFirstByOrderByChangedAtDesc();
	}
}