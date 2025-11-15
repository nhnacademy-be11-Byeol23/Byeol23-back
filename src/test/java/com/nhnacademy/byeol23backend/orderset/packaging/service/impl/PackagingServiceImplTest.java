package com.nhnacademy.byeol23backend.orderset.packaging.service.impl;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.*;
import static org.mockito.Mockito.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import com.nhnacademy.byeol23backend.image.domain.ImageDomain;
import com.nhnacademy.byeol23backend.image.dto.ImageUrlProjection;
import com.nhnacademy.byeol23backend.orderset.packaging.domain.Packaging;
import com.nhnacademy.byeol23backend.orderset.packaging.domain.dto.PackagingCreateRequest;
import com.nhnacademy.byeol23backend.orderset.packaging.domain.dto.PackagingCreateResponse;
import com.nhnacademy.byeol23backend.orderset.packaging.domain.dto.PackagingInfoResponse;
import com.nhnacademy.byeol23backend.orderset.packaging.domain.dto.PackagingUpdateRequest;
import com.nhnacademy.byeol23backend.orderset.packaging.exception.PackagingNotFoundException;
import com.nhnacademy.byeol23backend.orderset.packaging.repository.PackagingRepository;

@ExtendWith(MockitoExtension.class)
class PackagingServiceImplTest {

	@Mock
	private PackagingRepository packagingRepository;

	@InjectMocks
	private PackagingServiceImpl packagingService;

	private Packaging mockPackaging;
	private Long packagingId = 1L;

	@BeforeEach
	void setUp() {
		mockPackaging = Mockito.mock(Packaging.class);
	}

	// === ImageService 관련 테스트 ===

	@Test
	@DisplayName("이미지 URL 저장 성공")
	void saveImageUrl_Success() {
		// given
		String imageUrl = "http://test.com/image.png";
		given(packagingRepository.findById(packagingId)).willReturn(Optional.of(mockPackaging));

		// when
		packagingService.saveImageUrl(packagingId, imageUrl);

		// then
		verify(mockPackaging, times(1)).setPackagingImgUrl(imageUrl);
		verify(packagingRepository, times(1)).save(mockPackaging);
	}

	@Test
	@DisplayName("이미지 URL 저장 시 포장재 ID 없음")
	void saveImageUrl_NotFound_ThrowsException() {
		// given
		String imageUrl = "http://test.com/image.png";
		given(packagingRepository.findById(packagingId)).willReturn(Optional.empty());

		// when & then
		assertThatThrownBy(() -> packagingService.saveImageUrl(packagingId, imageUrl))
			.isInstanceOf(IllegalArgumentException.class)
			.hasMessageContaining("해당 포장재를 찾을 수 없습니다.");
		verify(packagingRepository, never()).save(any());
	}

	@Test
	@DisplayName("ID로 이미지 URL 조회 성공")
	void getImageUrlsById_Success() {
		// given
		String testUrl = "http://test.com/image.png";
		given(packagingRepository.findById(packagingId)).willReturn(Optional.of(mockPackaging));
		given(mockPackaging.getPackagingImgUrl()).willReturn(testUrl);

		// when
		List<ImageUrlProjection> result = packagingService.getImageUrlsById(packagingId);

		// then
		assertThat(result).hasSize(1);
		assertThat(result.get(0).getUrlId()).isEqualTo(packagingId);
		assertThat(result.get(0).getImageUrl()).isEqualTo(testUrl);
	}

	@Test
	@DisplayName("이미지 URL 삭제 성공")
	void deleteImageUrlsById_Success() {
		// given
		String testUrl = "http://test.com/image.png";
		given(packagingRepository.findById(packagingId)).willReturn(Optional.of(mockPackaging));
		given(mockPackaging.getPackagingImgUrl()).willReturn(testUrl); // 반환할 URL 설정

		// when
		String deletedUrl = packagingService.deleteImageUrlsById(packagingId);

		// then
		assertThat(deletedUrl).isEqualTo(testUrl);
		verify(mockPackaging, times(1)).setPackagingImgUrl(null); // URL이 null로 설정되었는지
		verify(packagingRepository, times(1)).save(mockPackaging); // 저장이 호출되었는지
	}

	@Test
	@DisplayName("지원하는 도메인 확인")
	void isSupportedDomain() {
		assertThat(packagingService.isSupportedDomain(ImageDomain.PACKAGING)).isTrue();
		assertThat(packagingService.isSupportedDomain(ImageDomain.BOOK)).isFalse();
	}

	// === PackagingService 관련 테스트 ===

	@Test
	@DisplayName("포장지 목록 페이징 조회 (getAllPacakings)")
	void getAllPacakings_Success() {
		// given
		Pageable pageable = PageRequest.of(0, 10);
		// Page 반환을 위해 실제 객체 또는 Mock 객체 생성
		given(mockPackaging.getPackagingId()).willReturn(1L);
		given(mockPackaging.getPackagingName()).willReturn("Test Packaging");
		given(mockPackaging.getPackagingPrice()).willReturn(new BigDecimal("1000"));
		given(mockPackaging.getPackagingImgUrl()).willReturn("url");

		Page<Packaging> mockPage = new PageImpl<>(List.of(mockPackaging), pageable, 1);
		given(packagingRepository.findAll(pageable)).willReturn(mockPage);

		// when
		Page<PackagingInfoResponse> result = packagingService.getAllPacakings(pageable);

		// then
		assertThat(result.getTotalElements()).isEqualTo(1);
		assertThat(result.getContent()).hasSize(1);
		assertThat(result.getContent().get(0).packagingName()).isEqualTo("Test Packaging");
		assertThat(result.getContent().get(0).packagingPrice()).isEqualByComparingTo("1000");
	}

	@Test
	@DisplayName("포장지 생성 (createPackaging)")
	void createPackaging_Success() {
		// given
		PackagingCreateRequest request = new PackagingCreateRequest("New Packaging", new BigDecimal("1500"));

		// ArgumentCaptor: Packaging.of()는 static이라 Mocking 불가.
		// save()에 전달되는 Packaging 객체를 캡처하여 검증
		ArgumentCaptor<Packaging> packagingCaptor = ArgumentCaptor.forClass(Packaging.class);

		// save()가 캡처된 객체를 반환하도록 설정 (ID 등이 할당된 것처럼)
		given(packagingRepository.save(packagingCaptor.capture())).willAnswer(invocation -> {
			Packaging savedPackaging = invocation.getArgument(0);
			// (실제라면 ID가 할당되겠지만, 여기서는 필드 검증이 목적)
			return savedPackaging;
		});

		// when
		PackagingCreateResponse response = packagingService.createPackaging(request);

		// then
		// 1. 캡처된 객체 검증
		Packaging capturedPackaging = packagingCaptor.getValue();
		assertThat(capturedPackaging.getPackagingName()).isEqualTo("New Packaging");
		assertThat(capturedPackaging.getPackagingPrice()).isEqualByComparingTo("1500");

		// 2. 응답 DTO 검증
		assertThat(response.packagingName()).isEqualTo("New Packaging");
	}

	@Test
	@DisplayName("포장지 수정 (updatePackaging)")
	void updatePackaging_Success() {
		// given
		PackagingUpdateRequest request = new PackagingUpdateRequest("Updated Name", new BigDecimal("2000"));
		given(packagingRepository.findById(packagingId)).willReturn(Optional.of(mockPackaging));

		// when
		packagingService.updatePackaging(packagingId, request);

		// then
		// 'request'의 새 값으로 'updateInfo'가 호출되었는지 검증 (버그 수정 후 기준)
		verify(mockPackaging, times(1)).updateInfo(eq("Updated Name"), eq(new BigDecimal("2000")));
	}

	@Test
	@DisplayName("포장지 수정 시 ID 없음")
	void updatePackaging_NotFound_ThrowsException() {
		// given
		PackagingUpdateRequest request = new PackagingUpdateRequest("Updated Name", new BigDecimal("2000"));
		given(packagingRepository.findById(packagingId)).willReturn(Optional.empty());

		// when & then
		assertThatThrownBy(() -> packagingService.updatePackaging(packagingId, request))
			.isInstanceOf(PackagingNotFoundException.class)
			.hasMessageContaining("해당 아이디의 포장지를 찾을 수 없습니다.");
		verify(mockPackaging, never()).updateInfo(any(), any());
	}

	@Test
	@DisplayName("포장지 삭제 (deletePackagingById)")
	void deletePackagingById_Success() {
		// given
		given(packagingRepository.existsById(packagingId)).willReturn(true);
		doNothing().when(packagingRepository).deleteById(packagingId);

		// when
		packagingService.deletePackagingById(packagingId);

		// then
		verify(packagingRepository, times(1)).existsById(packagingId);
		verify(packagingRepository, times(1)).deleteById(packagingId);
	}

	@Test
	@DisplayName("포장지 삭제 시 ID 없음")
	void deletePackagingById_NotFound_ThrowsException() {
		// given
		given(packagingRepository.existsById(packagingId)).willReturn(false);

		// when & then
		assertThatThrownBy(() -> packagingService.deletePackagingById(packagingId))
			.isInstanceOf(PackagingNotFoundException.class)
			.hasMessageContaining("해당 아이디의 포장지가 존재하지 않습니다.");
		verify(packagingRepository, never()).deleteById(anyLong());
	}

	@Test
	@DisplayName("포장지 전체 목록 조회 (getPackagingLists)")
	void getPackagingLists_Success() {
		// given
		given(mockPackaging.getPackagingId()).willReturn(1L);
		given(mockPackaging.getPackagingName()).willReturn("Test Packaging");
		given(mockPackaging.getPackagingPrice()).willReturn(new BigDecimal("1000"));
		given(mockPackaging.getPackagingImgUrl()).willReturn("url");

		List<Packaging> mockList = List.of(mockPackaging);
		given(packagingRepository.findAll()).willReturn(mockList);

		// when
		List<PackagingInfoResponse> result = packagingService.getPackagingLists();

		// then
		assertThat(result).hasSize(1);
		assertThat(result.get(0).packagingName()).isEqualTo("Test Packaging");
	}
}