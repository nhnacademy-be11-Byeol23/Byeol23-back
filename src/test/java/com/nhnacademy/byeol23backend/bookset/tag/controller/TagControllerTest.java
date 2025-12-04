package com.nhnacademy.byeol23backend.bookset.tag.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nhnacademy.byeol23backend.bookset.tag.domain.Tag;
import com.nhnacademy.byeol23backend.bookset.tag.domain.dto.AllTagsInfoResponse;
import com.nhnacademy.byeol23backend.bookset.tag.domain.dto.TagCreateRequest;
import com.nhnacademy.byeol23backend.bookset.tag.domain.dto.TagCreateResponse;
import com.nhnacademy.byeol23backend.bookset.tag.domain.dto.TagInfoResponse;
import com.nhnacademy.byeol23backend.bookset.tag.domain.dto.TagUpdateRequest;
import com.nhnacademy.byeol23backend.bookset.tag.domain.dto.TagUpdateResponse;
import com.nhnacademy.byeol23backend.bookset.tag.exception.TagAlreadyExistsException;
import com.nhnacademy.byeol23backend.bookset.tag.exception.TagNotFoundException;
import com.nhnacademy.byeol23backend.bookset.tag.service.TagService;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;

import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.*;
import org.springframework.http.MediaType;

import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;

import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import org.springframework.test.util.ReflectionTestUtils;

@WebMvcTest(TagController.class)
@Disabled
class TagControllerTest {

	@Autowired
	private MockMvc mockMvc;

	@MockBean
	private TagService tagService;

	private final ObjectMapper objectMapper = new ObjectMapper();

	// ───────────────────────── createTag ─────────────────────────

	@Test
	@DisplayName("POST /admin/tags - 태그 생성 성공")
	void createTag_Success() throws Exception {
		TagCreateRequest request = new TagCreateRequest("backend");

		Tag tag = new Tag("backend");
		ReflectionTestUtils.setField(tag, "tagId", 1L);
		TagCreateResponse response = new TagCreateResponse(tag);

		given(tagService.createTag(any(TagCreateRequest.class))).willReturn(response);

		mockMvc.perform(post("/admin/tags")
				.contentType(MediaType.APPLICATION_JSON)
				.content("""
                            {
                              "tagName": "backend"
                            }
                        """))
			.andExpect(status().isCreated())
			.andExpect(jsonPath("$.tag.tagId").value(1L))
			.andExpect(jsonPath("$.tag.tagName").value("backend"));

		verify(tagService, times(1)).createTag(any(TagCreateRequest.class));
	}

	@Test
	@DisplayName("POST /admin/tags - 태그 생성 실패(이미 존재)")
	void createTag_Fail_TagAlreadyExists() throws Exception {
		given(tagService.createTag(any(TagCreateRequest.class)))
			.willThrow(new TagAlreadyExistsException("Tag가 이미 존재합니다"));

		mockMvc.perform(post("/admin/tags")
				.contentType(MediaType.APPLICATION_JSON)
				.content("""
                            {
                              "tagName": "backend"
                            }
                        """))
			.andExpect(status().isConflict())
			.andExpect(jsonPath("$.message").value("Tag가 이미 존재합니다"));

		verify(tagService, times(1)).createTag(any(TagCreateRequest.class));
	}

	// ───────────────────────── getTagByTagId ─────────────────────────

	@Test
	@DisplayName("GET /admin/tags/{tagId} - 태그 단건 조회 성공")
	void getTagByTagId_Success() throws Exception {
		Long tagId = 1L;

		Tag tag = new Tag("backend");
		ReflectionTestUtils.setField(tag, "tagId", tagId);
		TagInfoResponse response = new TagInfoResponse(tag);

		given(tagService.getTagByTagId(tagId)).willReturn(response);

		mockMvc.perform(get("/admin/tags/{tagId}", tagId))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.tag.tagId").value(1L))
			.andExpect(jsonPath("$.tag.tagName").value("backend"));

		verify(tagService, times(1)).getTagByTagId(tagId);
	}

	// ───────────────────────── deleteTagByTagId ─────────────────────────

	@Test
	@DisplayName("DELETE /admin/tags/{tagId} - 태그 삭제 성공")
	void deleteTagByTagId_Success() throws Exception {
		Long tagId = 1L;

		willDoNothing().given(tagService).deleteTagByTagId(tagId);

		mockMvc.perform(delete("/admin/tags/{tagId}", tagId))
			.andExpect(status().isNoContent());

		verify(tagService, times(1)).deleteTagByTagId(tagId);
	}

	@Test
	@DisplayName("DELETE /admin/tags/{tagId} - 태그 삭제 실패(존재하지 않음)")
	void deleteTagByTagId_Fail_TagNotFound() throws Exception {
		Long tagId = 999L;

		willThrow(new TagNotFoundException("해당 아이디 태그를 찾을 수 없습니다"))
			.given(tagService).deleteTagByTagId(tagId);

		mockMvc.perform(delete("/admin/tags/{tagId}", tagId))
			.andExpect(status().isNotFound())
			.andExpect(jsonPath("$.message").value("해당 아이디 태그를 찾을 수 없습니다"));

		verify(tagService, times(1)).deleteTagByTagId(tagId);
	}

	// ───────────────────────── updateTagByTagId ─────────────────────────

	@Test
	@DisplayName("PUT /admin/tags/{tagId} - 태그 수정 성공")
	void updateTagByTagId_Success() throws Exception {
		Long tagId = 1L;
		TagUpdateRequest request = new TagUpdateRequest("new-name");

		Tag updatedTag = new Tag("new-name");
		ReflectionTestUtils.setField(updatedTag, "tagId", tagId);
		TagUpdateResponse response = new TagUpdateResponse(updatedTag);

		given(tagService.updateTagByTagId(eq(tagId), any(TagUpdateRequest.class)))
			.willReturn(response);

		mockMvc.perform(put("/admin/tags/{tagId}", tagId)
				.contentType(MediaType.APPLICATION_JSON)
				.content("""
                            {
                              "tagName": "new-name"
                            }
                        """))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.tag.tagId").value(1L))
			.andExpect(jsonPath("$.tag.tagName").value("new-name"));

		verify(tagService, times(1))
			.updateTagByTagId(eq(tagId), any(TagUpdateRequest.class));
	}

	@Test
	@DisplayName("PUT /admin/tags/{tagId} - 태그 수정 실패(이미 존재하는 이름)")
	void updateTagByTagId_Fail_TagAlreadyExists() throws Exception {
		Long tagId = 1L;

		given(tagService.updateTagByTagId(eq(tagId), any(TagUpdateRequest.class)))
			.willThrow(new TagAlreadyExistsException("Tag가 이미 존재합니다"));

		mockMvc.perform(put("/admin/tags/{tagId}", tagId)
				.contentType(MediaType.APPLICATION_JSON)
				.content("""
                            {
                              "tagName": "duplicate-name"
                            }
                        """))
			.andExpect(status().isConflict())
			.andExpect(jsonPath("$.message").value("Tag가 이미 존재합니다"));

		verify(tagService, times(1))
			.updateTagByTagId(eq(tagId), any(TagUpdateRequest.class));
	}

	// ───────────────────────── getAllTags ─────────────────────────

	@Test
	@DisplayName("GET /admin/tags - 태그 전체 조회 성공")
	void getAllTags_Success() throws Exception {
		Pageable pageable = PageRequest.of(0, 10);

		AllTagsInfoResponse tag1 = new AllTagsInfoResponse(1L, "backend");
		AllTagsInfoResponse tag2 = new AllTagsInfoResponse(2L, "frontend");

		Page<AllTagsInfoResponse> page =
			new PageImpl<>(List.of(tag1, tag2), pageable, 2);

		given(tagService.getAllTags(any(Pageable.class))).willReturn(page);

		mockMvc.perform(get("/admin/tags")
				.param("page", "0")
				.param("size", "10"))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.content", org.hamcrest.Matchers.hasSize(2)))
			.andExpect(jsonPath("$.content[0].tagId").value(1L))
			.andExpect(jsonPath("$.content[0].tagName").value("backend"))
			.andExpect(jsonPath("$.content[1].tagId").value(2L))
			.andExpect(jsonPath("$.content[1].tagName").value("frontend"));

		verify(tagService, times(1)).getAllTags(any(Pageable.class));
	}
}
