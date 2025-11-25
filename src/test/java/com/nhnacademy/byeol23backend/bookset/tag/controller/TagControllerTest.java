package com.nhnacademy.byeol23backend.bookset.tag.controller;

import com.nhnacademy.byeol23backend.bookset.tag.domain.Tag;
import com.nhnacademy.byeol23backend.bookset.tag.domain.dto.AllTagsInfoResponse;
import com.nhnacademy.byeol23backend.bookset.tag.domain.dto.TagCreateRequest;
import com.nhnacademy.byeol23backend.bookset.tag.domain.dto.TagCreateResponse;
import com.nhnacademy.byeol23backend.bookset.tag.domain.dto.TagInfoResponse;
import com.nhnacademy.byeol23backend.bookset.tag.domain.dto.TagUpdateRequest;
import com.nhnacademy.byeol23backend.bookset.tag.domain.dto.TagUpdateResponse;
import com.nhnacademy.byeol23backend.bookset.tag.service.TagService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.*;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.List;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.*;

@ExtendWith(MockitoExtension.class)
class TagControllerTest {

	@Mock
	private TagService tagService;

	@InjectMocks
	private TagController tagController;

	// ───────────────────────── GET /api/tags/{tag-id} ─────────────────────────

	@Test
	@DisplayName("태그 단건 조회 성공 - GET /api/tags/{tag-id}")
	void getTagByTagId_Success() {
		// given
		Long tagId = 1L;
		TagInfoResponse response = new TagInfoResponse(new Tag("backend"));

		given(tagService.getTagByTagId(tagId)).willReturn(response);

		// when
		ResponseEntity<TagInfoResponse> result = tagController.getTagByTagId(tagId);

		// then
		assertThat(result.getStatusCode()).isEqualTo(HttpStatus.OK);
		assertThat(result.getBody()).isNotNull();
		TagInfoResponse body = result.getBody();
		assertThat(body.tag().getTagName()).isEqualTo("backend");

		verify(tagService, times(1)).getTagByTagId(tagId);
	}

	@Test
	@DisplayName("태그 단건 조회 실패 - 존재하지 않는 태그")
	void getTagByTagId_Fail_NotFound() {
		// given
		Long tagId = 999L;

		// 실제로는 TagNotFoundException 같은 커스텀 예외일 가능성이 큼
		given(tagService.getTagByTagId(tagId))
			.willThrow(new RuntimeException("존재하지 않는 태그입니다: " + tagId));

		// when & then
		assertThatThrownBy(() -> tagController.getTagByTagId(tagId))
			.isInstanceOf(RuntimeException.class)
			.hasMessageContaining("존재하지 않는 태그");

		verify(tagService, times(1)).getTagByTagId(tagId);
	}

	// ───────────────────────── POST /api/tags (생성) ─────────────────────────

	@Test
	@DisplayName("태그 생성 성공 - POST /api/tags")
	void createTag_Success() {
		// given
		TagCreateRequest request = new TagCreateRequest("backend");
		TagCreateResponse response = new TagCreateResponse(new Tag("backend"));

		given(tagService.createTag(any(TagCreateRequest.class))).willReturn(response);

		// when
		ResponseEntity<TagCreateResponse> result = tagController.createTag(request);

		// then
		assertThat(result.getStatusCode()).isEqualTo(HttpStatus.CREATED);
		assertThat(result.getBody()).isNotNull();
		TagCreateResponse body = result.getBody();
		assertThat(body.tag().getTagName()).isEqualTo("backend");

		// Location 헤더도 검증 (Controller에서 tagName 기준으로 URI 생성 중)
		assertThat(result.getHeaders().getLocation()).isNotNull();
		assertThat(result.getHeaders().getLocation().toString()).isEqualTo("/api/tags/backend");

		verify(tagService, times(1)).createTag(request);
	}

	@Test
	@DisplayName("태그 생성 실패 - 서비스 예외 발생")
	void createTag_Fail_ServiceException() {
		// given
		TagCreateRequest request = new TagCreateRequest("backend");

		given(tagService.createTag(any(TagCreateRequest.class)))
			.willThrow(new RuntimeException("태그 생성 실패"));

		// when & then
		assertThatThrownBy(() -> tagController.createTag(request))
			.isInstanceOf(RuntimeException.class)
			.hasMessageContaining("태그 생성 실패");

		verify(tagService, times(1)).createTag(request);
	}

	// ───────────────────────── POST /api/tags/delete/{tag-id} (삭제) ─────────────────────────

	@Test
	@DisplayName("태그 삭제 성공 - POST /api/tags/delete/{tag-id}")
	void deleteTagByTagId_Success() {
		// given
		Long tagId = 1L;
		willDoNothing().given(tagService).deleteTagByTagId(tagId);

		// when
		ResponseEntity<Void> result = tagController.deleteTagByTagId(tagId);

		// then
		assertThat(result.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
		assertThat(result.getBody()).isNull();

		verify(tagService, times(1)).deleteTagByTagId(tagId);
	}

	@Test
	@DisplayName("태그 삭제 실패 - 존재하지 않는 태그")
	void deleteTagByTagId_Fail_NotFound() {
		// given
		Long tagId = 999L;
		willThrow(new RuntimeException("존재하지 않는 태그입니다: " + tagId))
			.given(tagService).deleteTagByTagId(tagId);

		// when & then
		assertThatThrownBy(() -> tagController.deleteTagByTagId(tagId))
			.isInstanceOf(RuntimeException.class)
			.hasMessageContaining("존재하지 않는 태그");

		verify(tagService, times(1)).deleteTagByTagId(tagId);
	}

	// ───────────────────────── POST /api/tags/put/{tag-id} (수정) ─────────────────────────

	@Test
	@DisplayName("태그 수정 성공 - POST /api/tags/put/{tag-id}")
	void updateTagByTagId_Success() {
		// given
		Long tagId = 1L;
		TagUpdateRequest updateRequest = new TagUpdateRequest("new-name");
		TagUpdateResponse response = new TagUpdateResponse(new Tag("new-name"));

		given(tagService.updateTagByTagId(tagId, updateRequest)).willReturn(response);

		// when
		ResponseEntity<TagUpdateResponse> result = tagController.updateTagByTagId(tagId, updateRequest);

		// then
		assertThat(result.getStatusCode()).isEqualTo(HttpStatus.OK);
		assertThat(result.getBody()).isNotNull();
		TagUpdateResponse body = result.getBody();
		assertThat(body.tag().getTagName()).isEqualTo("new-name");

		verify(tagService, times(1)).updateTagByTagId(tagId, updateRequest);
	}

	@Test
	@DisplayName("태그 수정 실패 - 존재하지 않는 태그")
	void updateTagByTagId_Fail_NotFound() {
		// given
		Long tagId = 999L;
		TagUpdateRequest updateRequest = new TagUpdateRequest("new-name");

		given(tagService.updateTagByTagId(tagId, updateRequest))
			.willThrow(new RuntimeException("존재하지 않는 태그입니다: " + tagId));

		// when & then
		assertThatThrownBy(() -> tagController.updateTagByTagId(tagId, updateRequest))
			.isInstanceOf(RuntimeException.class)
			.hasMessageContaining("존재하지 않는 태그");

		verify(tagService, times(1)).updateTagByTagId(tagId, updateRequest);
	}

	// ───────────────────────── GET /api/tags (목록 조회) ─────────────────────────

	@Test
	@DisplayName("태그 목록 조회 성공 - GET /api/tags")
	void getAllTags_Success() {
		// given
		AllTagsInfoResponse tag1 = new AllTagsInfoResponse(1L, "backend");
		AllTagsInfoResponse tag2 = new AllTagsInfoResponse(2L, "frontend");

		Pageable pageable = PageRequest.of(0, 10);
		Page<AllTagsInfoResponse> page = new PageImpl<>(List.of(tag1, tag2), pageable, 2);

		given(tagService.getAllTags(any(Pageable.class))).willReturn(page);

		// when
		ResponseEntity<Page<AllTagsInfoResponse>> result = tagController.getAllTags(0, 10);

		// then
		assertThat(result.getStatusCode()).isEqualTo(HttpStatus.OK);
		assertThat(result.getBody()).isNotNull();
		Page<AllTagsInfoResponse> body = result.getBody();
		assertThat(body.getContent()).hasSize(2);
		assertThat(body.getContent().get(0).tagId()).isEqualTo(1L);
		assertThat(body.getContent().get(0).tagName()).isEqualTo("backend");
		assertThat(body.getContent().get(1).tagId()).isEqualTo(2L);
		assertThat(body.getContent().get(1).tagName()).isEqualTo("frontend");
		assertThat(body.getTotalElements()).isEqualTo(2);

		verify(tagService, times(1)).getAllTags(any(Pageable.class));
	}

	@Test
	@DisplayName("태그 목록 조회 성공 - 빈 목록")
	void getAllTags_Success_Empty() {
		// given
		Pageable pageable = PageRequest.of(0, 10);
		Page<AllTagsInfoResponse> emptyPage = Page.empty(pageable);

		given(tagService.getAllTags(any(Pageable.class))).willReturn(emptyPage);

		// when
		ResponseEntity<Page<AllTagsInfoResponse>> result = tagController.getAllTags(0, 10);

		// then
		assertThat(result.getStatusCode()).isEqualTo(HttpStatus.OK);
		assertThat(result.getBody()).isNotNull();
		assertThat(result.getBody().getContent()).isEmpty();

		verify(tagService, times(1)).getAllTags(any(Pageable.class));
	}
}
