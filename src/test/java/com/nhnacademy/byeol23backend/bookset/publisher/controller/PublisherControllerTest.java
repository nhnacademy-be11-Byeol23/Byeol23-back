package com.nhnacademy.byeol23backend.bookset.publisher.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import java.net.URI;
import java.util.List;
import java.util.Optional;

import com.nhnacademy.byeol23backend.bookset.publisher.domain.dto.AllPublishersInfoResponse;
import com.nhnacademy.byeol23backend.bookset.publisher.domain.dto.PublisherCreateRequest;
import com.nhnacademy.byeol23backend.bookset.publisher.domain.dto.PublisherCreateResponse;
import com.nhnacademy.byeol23backend.bookset.publisher.domain.dto.PublisherInfoResponse;
import com.nhnacademy.byeol23backend.bookset.publisher.domain.dto.PublisherUpdateRequest;
import com.nhnacademy.byeol23backend.bookset.publisher.domain.dto.PublisherUpdateResponse;
import com.nhnacademy.byeol23backend.bookset.publisher.service.PublisherService;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

@ExtendWith(MockitoExtension.class)
class PublisherControllerTest {

	@Mock
	private PublisherService publisherService;

	@InjectMocks
	private PublisherController publisherController;

	@Test
	@DisplayName("GET /api/pub/{publisher-id} - 출판사 단건 조회 성공")
	void getPublisherByPublisherId_Success() {
		// given
		Long publisherId = 1L;
		PublisherInfoResponse mockResponse = org.mockito.Mockito.mock(PublisherInfoResponse.class);

		given(publisherService.getPublisherByPublisherId(publisherId))
			.willReturn(mockResponse);

		// when
		ResponseEntity<PublisherInfoResponse> result =
			publisherController.getPublisherByPublisherId(publisherId);

		// then
		assertThat(result.getStatusCode()).isEqualTo(HttpStatus.OK);
		assertThat(result.getBody()).isSameAs(mockResponse);
		verify(publisherService, times(1)).getPublisherByPublisherId(publisherId);
	}

	@Test
	@DisplayName("POST /api/pub - 출판사 생성 성공")
	void createPublisher_Success() {
		// given
		PublisherCreateRequest request = new PublisherCreateRequest("민음사"); // 시그니처에 맞게 수정
		PublisherCreateResponse mockResponse = org.mockito.Mockito.mock(PublisherCreateResponse.class);

		given(publisherService.createPublisher(request))
			.willReturn(mockResponse);

		// when
		ResponseEntity<PublisherCreateResponse> result =
			publisherController.createPublisher(request);

		// then
		assertThat(result.getStatusCode()).isEqualTo(HttpStatus.CREATED);
		assertThat(result.getBody()).isSameAs(mockResponse);

		String encodedName = java.net.URLEncoder.encode(
			request.publisherName(), java.nio.charset.StandardCharsets.UTF_8
		);

		URI location = result.getHeaders().getLocation();
		assertThat(location).isNotNull();
		assertThat(location.toString()).isEqualTo("/api/pub/" + encodedName);

		verify(publisherService, times(1)).createPublisher(request);
	}

	@Test
	@DisplayName("POST /api/pub/delete/{publisher-id} - 출판사 삭제 성공")
	void deletePublisherByPublisherId_Success() {
		// given
		Long publisherId = 1L;

		// when
		ResponseEntity<Void> result =
			publisherController.deletePublisherByPublisherId(publisherId);

		// then
		assertThat(result.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
		assertThat(result.getBody()).isNull();

		verify(publisherService, times(1)).deletePublisherByPublisherId(publisherId);
	}

	@Test
	@DisplayName("POST /api/pub/put/{publisher-id} - 출판사 수정 성공")
	void updatePublisherByPublisherId_Success() {
		// given
		Long publisherId = 1L;
		PublisherUpdateRequest request = new PublisherUpdateRequest("새 출판사명"); // 실제 시그니처에 맞게 수정
		PublisherUpdateResponse mockResponse = org.mockito.Mockito.mock(PublisherUpdateResponse.class);

		given(publisherService.updatePublisherByPublisherId(publisherId, request))
			.willReturn(mockResponse);

		// when
		ResponseEntity<PublisherUpdateResponse> result =
			publisherController.updatePublisherByPublisherId(publisherId, request);

		// then
		assertThat(result.getStatusCode()).isEqualTo(HttpStatus.OK);
		assertThat(result.getBody()).isSameAs(mockResponse);

		verify(publisherService, times(1)).updatePublisherByPublisherId(publisherId, request);
	}

	@Test
	@DisplayName("GET /api/pub - 출판사 목록 조회 성공")
	void getAllPublishers_Success() {
		// given
		int page = 0;
		int size = 20;
		Pageable pageable = PageRequest.of(page, size);

		AllPublishersInfoResponse pub1 = new AllPublishersInfoResponse(1L, "민음사");
		AllPublishersInfoResponse pub2 = new AllPublishersInfoResponse(2L, "문학동네");

		Page<AllPublishersInfoResponse> mockPage =
			new PageImpl<>(List.of(pub1, pub2), pageable, 2);

		given(publisherService.getAllPublishers(pageable))
			.willReturn(mockPage);

		// when
		ResponseEntity<Page<AllPublishersInfoResponse>> result =
			publisherController.getAllPublishers(page, size);

		// then
		assertThat(result.getStatusCode()).isEqualTo(HttpStatus.OK);
		assertThat(result.getBody()).isNotNull();
		Page<AllPublishersInfoResponse> body = result.getBody();
		assertThat(body.getContent()).hasSize(2);
		assertThat(body.getContent().get(0).publisherId()).isEqualTo(1L);
		assertThat(body.getContent().get(0).publisherName()).isEqualTo("민음사");

		verify(publisherService, times(1)).getAllPublishers(pageable);
	}

	@Test
	@DisplayName("GET /api/pub/search?publisherName=민음사 - 이름으로 출판사 찾기 성공 (200)")
	void findPublisherByName_Found_ReturnsOk() {
		// given
		String name = "민음사";
		AllPublishersInfoResponse response = new AllPublishersInfoResponse(1L, name);

		given(publisherService.findPublisherByName(name))
			.willReturn(Optional.of(response));

		// when
		ResponseEntity<AllPublishersInfoResponse> result =
			publisherController.findPublisherByName(name);

		// then
		assertThat(result.getStatusCode()).isEqualTo(HttpStatus.OK);
		assertThat(result.getBody()).isNotNull();
		assertThat(result.getBody().publisherId()).isEqualTo(1L);
		assertThat(result.getBody().publisherName()).isEqualTo(name);

		verify(publisherService, times(1)).findPublisherByName(name);
	}

	@Test
	@DisplayName("GET /api/pub/search?publisherName=없는출판사 - 존재하지 않으면 404")
	void findPublisherByName_NotFound_Returns404() {
		// given
		String name = "없는출판사";
		given(publisherService.findPublisherByName(name))
			.willReturn(Optional.empty());

		// when
		ResponseEntity<AllPublishersInfoResponse> result =
			publisherController.findPublisherByName(name);

		// then
		assertThat(result.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
		assertThat(result.getBody()).isNull();

		verify(publisherService, times(1)).findPublisherByName(name);
	}

	@Test
	@DisplayName("POST /api/pub/find-or-create - 출판사 findOrCreate 성공")
	void findOrCreatePublisher_Success() {
		// given
		PublisherCreateRequest request = new PublisherCreateRequest("민음사");
		AllPublishersInfoResponse response = new AllPublishersInfoResponse(1L, "민음사");

		given(publisherService.findOrCreatePublisher(request.publisherName()))
			.willReturn(response);

		// when
		ResponseEntity<AllPublishersInfoResponse> result =
			publisherController.findOrCreatePublisher(request);

		// then
		assertThat(result.getStatusCode()).isEqualTo(HttpStatus.OK);
		assertThat(result.getBody()).isNotNull();
		assertThat(result.getBody().publisherId()).isEqualTo(1L);
		assertThat(result.getBody().publisherName()).isEqualTo("민음사");

		verify(publisherService, times(1)).findOrCreatePublisher(request.publisherName());
	}
}
