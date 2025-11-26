package com.nhnacademy.byeol23backend.bookset.contributor.controller;

import com.nhnacademy.byeol23backend.bookset.contributor.domain.Contributor;
import com.nhnacademy.byeol23backend.bookset.contributor.domain.ContributorRole;
import com.nhnacademy.byeol23backend.bookset.contributor.domain.dto.*;
import com.nhnacademy.byeol23backend.bookset.contributor.service.ContributorService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.net.URI;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.willDoNothing;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class ContributorControllerTest {

	@Mock
	private ContributorService contributorService;

	@InjectMocks
	private ContributorController contributorController;

	// ───────────────────────── GET /api/cont/{id} ─────────────────────────

	@Test
	@DisplayName("GET /api/cont/{id} - 기여자 단건 조회 성공")
	void getContributorByContributorId_Success() {
		// given
		Long contributorId = 1L;
		Contributor contributor =
			new Contributor(contributorId, "저자명", ContributorRole.AUTHOR);
		ContributorInfoResponse infoResponse = new ContributorInfoResponse(contributor);

		given(contributorService.getContributorByContributorId(contributorId))
			.willReturn(infoResponse);

		// when
		ResponseEntity<ContributorInfoResponse> result =
			contributorController.getContributorByContributorId(contributorId);

		// then
		assertThat(result.getStatusCode()).isEqualTo(HttpStatus.OK);
		assertThat(result.getBody()).isNotNull();
		assertThat(result.getBody().contributor().getContributorId()).isEqualTo(contributorId);
		assertThat(result.getBody().contributor().getContributorName()).isEqualTo("저자명");
		assertThat(result.getBody().contributor().getContributorRole())
			.isEqualTo(ContributorRole.AUTHOR);

		verify(contributorService, times(1))
			.getContributorByContributorId(contributorId);
	}

	// ───────────────────────── POST /api/cont ─────────────────────────

	@Test
	@DisplayName("POST /api/cont - 기여자 생성 성공")
	void createContributor_Success() {
		// given
		ContributorCreateRequest request =
			new ContributorCreateRequest("새 저자", ContributorRole.AUTHOR);

		Contributor contributor =
			new Contributor(10L, "새 저자", ContributorRole.AUTHOR);
		ContributorCreateResponse serviceResponse =
			new ContributorCreateResponse(contributor);

		given(contributorService.createContributor(request))
			.willReturn(serviceResponse);

		// when
		ResponseEntity<ContributorCreateResponse> result =
			contributorController.createContributor(request);

		// then
		assertThat(result.getStatusCode()).isEqualTo(HttpStatus.CREATED);
		assertThat(result.getHeaders().getLocation())
			.isEqualTo(URI.create("/api/cont/" + contributor.getContributorId()));
		assertThat(result.getBody()).isNotNull();
		assertThat(result.getBody().contributor().getContributorId()).isEqualTo(10L);
		assertThat(result.getBody().contributor().getContributorName()).isEqualTo("새 저자");
		assertThat(result.getBody().contributor().getContributorRole())
			.isEqualTo(ContributorRole.AUTHOR);

		verify(contributorService, times(1))
			.createContributor(request);
	}

	// ───────────────────────── POST /api/cont/find-or-create ─────────────────────────

	@Test
	@DisplayName("POST /api/cont/find-or-create - 기여자 찾기 또는 생성 성공")
	void findOrCreateContributor_Success() {
		// given
		ContributorFindOrCreateRequest request =
			new ContributorFindOrCreateRequest("저자명", ContributorRole.AUTHOR);

		Long contributorId = 5L;
		Contributor contributor =
			new Contributor(contributorId, "저자명", ContributorRole.AUTHOR);
		ContributorInfoResponse infoResponse =
			new ContributorInfoResponse(contributor);

		given(contributorService.findOrCreateContributor("저자명", ContributorRole.AUTHOR))
			.willReturn(contributorId);
		given(contributorService.getContributorByContributorId(contributorId))
			.willReturn(infoResponse);

		// when
		ResponseEntity<AllContributorResponse> result =
			contributorController.findOrCreateContributor(request);

		// then
		assertThat(result.getStatusCode()).isEqualTo(HttpStatus.OK);
		assertThat(result.getBody()).isNotNull();
		AllContributorResponse body = result.getBody();
		assertThat(body.contributorId()).isEqualTo(contributorId);
		assertThat(body.contributorName()).isEqualTo("저자명");
		// AllContributorResponse.contributorRole() 가 String 이라고 가정
		assertThat(body.contributorRole()).isEqualTo(ContributorRole.AUTHOR.getLabel());

		verify(contributorService, times(1))
			.findOrCreateContributor("저자명", ContributorRole.AUTHOR);
		verify(contributorService, times(1))
			.getContributorByContributorId(contributorId);
	}

	// ───────────────────────── POST /api/cont/delete/{id} ─────────────────────────

	@Test
	void name() {
	}

	@Test
	@DisplayName("POST /api/cont/delete/{id} - 기여자 삭제 성공")
	void deleteContributorByContributorId_Success() {
		// given
		Long contributorId = 3L;
		willDoNothing().given(contributorService)
			.deleteContributorByContributorId(contributorId);

		// when
		ResponseEntity<Void> result =
			contributorController.deleteContributorByContributorId(contributorId);

		// then
		assertThat(result.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
		assertThat(result.getBody()).isNull();

		verify(contributorService, times(1))
			.deleteContributorByContributorId(contributorId);
	}

	// ───────────────────────── POST /api/cont/put/{id} ─────────────────────────

	@Test
	@DisplayName("POST /api/cont/put/{id} - 기여자 수정 성공")
	void updateContributor_Success() {
		// given
		Long contributorId = 7L;
		ContributorUpdateRequest request =
			new ContributorUpdateRequest("수정된이름", "TRANSLATOR");

		Contributor contributor =
			new Contributor(contributorId, "수정된이름", ContributorRole.TRANSLATOR);
		ContributorUpdateResponse serviceResponse =
			new ContributorUpdateResponse(contributor);

		given(contributorService.updateContributor(contributorId, request))
			.willReturn(serviceResponse);

		// when
		ResponseEntity<ContributorUpdateResponse> result =
			contributorController.updateContributor(contributorId, request);

		// then
		assertThat(result.getStatusCode()).isEqualTo(HttpStatus.OK);
		assertThat(result.getBody()).isNotNull();
		assertThat(result.getBody().contributor().getContributorId()).isEqualTo(contributorId);
		assertThat(result.getBody().contributor().getContributorName()).isEqualTo("수정된이름");
		assertThat(result.getBody().contributor().getContributorRole())
			.isEqualTo(ContributorRole.TRANSLATOR);

		verify(contributorService, times(1))
			.updateContributor(contributorId, request);
	}

	// ───────────────────────── GET /api/cont?page=&size= ─────────────────────────

	@Test
	@DisplayName("GET /api/cont?page=&size= - 기여자 목록 조회 성공")
	void getAllContributors_Success() {
		// given
		int page = 0;
		int size = 10;
		PageRequest pageRequest = PageRequest.of(page, size);

		AllContributorResponse c1 =
			new AllContributorResponse(1L, "저자1", "AUTHOR");
		AllContributorResponse c2 =
			new AllContributorResponse(2L, "저자2", "TRANSLATOR");

		Page<AllContributorResponse> pageResult =
			new PageImpl<>(List.of(c1, c2), pageRequest, 2);

		given(contributorService.getAllContributors(page, size))
			.willReturn(pageResult);

		// when
		ResponseEntity<Page<AllContributorResponse>> result =
			contributorController.getAllContributors(page, size);

		// then
		assertThat(result.getStatusCode()).isEqualTo(HttpStatus.OK);
		assertThat(result.getBody()).isNotNull();
		Page<AllContributorResponse> body = result.getBody();
		assertThat(body.getContent()).hasSize(2);
		assertThat(body.getContent().get(0).contributorId()).isEqualTo(1L);
		assertThat(body.getContent().get(1).contributorId()).isEqualTo(2L);

		verify(contributorService, times(1))
			.getAllContributors(page, size);
	}
}
