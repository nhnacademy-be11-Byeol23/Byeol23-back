package com.nhnacademy.byeol23backend.bookset.tag.service.impl;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.*;

import java.util.List;
import java.util.Optional;

import com.nhnacademy.byeol23backend.bookset.booktag.repository.BookTagRepository;
import com.nhnacademy.byeol23backend.bookset.tag.domain.Tag;
import com.nhnacademy.byeol23backend.bookset.tag.domain.dto.AllTagsInfoResponse;
import com.nhnacademy.byeol23backend.bookset.tag.domain.dto.TagCreateRequest;
import com.nhnacademy.byeol23backend.bookset.tag.domain.dto.TagCreateResponse;
import com.nhnacademy.byeol23backend.bookset.tag.domain.dto.TagInfoResponse;
import com.nhnacademy.byeol23backend.bookset.tag.domain.dto.TagUpdateRequest;
import com.nhnacademy.byeol23backend.bookset.tag.domain.dto.TagUpdateResponse;
import com.nhnacademy.byeol23backend.bookset.tag.exception.TagAlreadyExistsException;
import com.nhnacademy.byeol23backend.bookset.tag.exception.TagNotFoundException;
import com.nhnacademy.byeol23backend.bookset.tag.repository.TagRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.*;
import org.springframework.test.util.ReflectionTestUtils;

@ExtendWith(MockitoExtension.class)
class TagServiceImplTest {

	@Mock
	private TagRepository tagRepository;

	@Mock
	private BookTagRepository bookTagRepository;

	@InjectMocks
	private TagServiceImpl tagService;

	// ───────────────────────── createTag ─────────────────────────

	@Test
	@DisplayName("태그 생성 성공 - 중복 이름 없음")
	void createTag_Success() {
		// given
		TagCreateRequest request = new TagCreateRequest("backend");

		// 중복 없음
		given(tagRepository.findTagByTagName("backend")).willReturn(0L);

		// save 호출 시 ID 세팅
		given(tagRepository.save(any(Tag.class))).willAnswer(invocation -> {
			Tag tag = invocation.getArgument(0);
			ReflectionTestUtils.setField(tag, "tagId", 1L);
			return tag;
		});

		// when
		TagCreateResponse result = tagService.createTag(request);

		// then
		assertThat(result).isNotNull();
		Tag savedTag = result.tag();
		assertThat(savedTag.getTagId()).isEqualTo(1L);
		assertThat(savedTag.getTagName()).isEqualTo("backend");

		verify(tagRepository, times(1)).findTagByTagName("backend");
		verify(tagRepository, times(1)).save(any(Tag.class));
	}

	@Test
	@DisplayName("태그 생성 실패 - 이미 존재하는 이름")
	void createTag_Fail_TagAlreadyExists() {
		// given
		TagCreateRequest request = new TagCreateRequest("backend");

		// 중복 존재
		given(tagRepository.findTagByTagName("backend")).willReturn(1L);

		// when & then
		assertThatThrownBy(() -> tagService.createTag(request))
			.isInstanceOf(TagAlreadyExistsException.class)
			.hasMessageContaining("Tag가 이미 존재합니다");

		verify(tagRepository, times(1)).findTagByTagName("backend");
		verify(tagRepository, never()).save(any(Tag.class));
	}

	// ───────────────────────── getTagByTagId ─────────────────────────

	@Test
	@DisplayName("태그 단건 조회 성공")
	void getTagByTagId_Success() {
		// given
		Long tagId = 1L;
		Tag tag = new Tag("backend");
		ReflectionTestUtils.setField(tag, "tagId", tagId);

		given(tagRepository.getTagByTagId(tagId)).willReturn(tag);

		// when
		TagInfoResponse result = tagService.getTagByTagId(tagId);

		// then
		assertThat(result).isNotNull();
		Tag foundTag = result.tag();
		assertThat(foundTag.getTagId()).isEqualTo(tagId);
		assertThat(foundTag.getTagName()).isEqualTo("backend");

		verify(tagRepository, times(1)).getTagByTagId(tagId);
	}

	// 현재 구현은 getTagByTagId에서 null일 때 예외를 던지지 않으므로, null 케이스는 생략해도 됨
	// 필요하면 서비스 로직을 바꾸고 테스트를 추가할 수 있음.

	// ───────────────────────── deleteTagByTagId ─────────────────────────

	@Test
	@DisplayName("태그 삭제 성공")
	void deleteTagByTagId_Success() {
		// given
		Long tagId = 1L;
		Tag tag = new Tag("backend");
		ReflectionTestUtils.setField(tag, "tagId", tagId);

		given(tagRepository.findTagByTagId(tagId)).willReturn(Optional.of(tag));
		willDoNothing().given(bookTagRepository).deleteByTagId(tagId);
		willDoNothing().given(tagRepository).deleteTagByTagId(tagId);

		// when
		tagService.deleteTagByTagId(tagId);

		// then
		verify(tagRepository, times(1)).findTagByTagId(tagId);
		verify(bookTagRepository, times(1)).deleteByTagId(tagId);
		verify(tagRepository, times(1)).deleteTagByTagId(tagId);
	}

	@Test
	@DisplayName("태그 삭제 실패 - 존재하지 않는 태그")
	void deleteTagByTagId_Fail_TagNotFound() {
		// given
		Long tagId = 999L;
		given(tagRepository.findTagByTagId(tagId)).willReturn(Optional.empty());

		// when & then
		assertThatThrownBy(() -> tagService.deleteTagByTagId(tagId))
			.isInstanceOf(TagNotFoundException.class)
			.hasMessageContaining("해당 아이디 태그를 찾을 수 없습니다");

		verify(tagRepository, times(1)).findTagByTagId(tagId);
		verify(bookTagRepository, never()).deleteByTagId(anyLong());
		verify(tagRepository, never()).deleteTagByTagId(anyLong());
	}

	// ───────────────────────── updateTagByTagId ─────────────────────────

	@Test
	@DisplayName("태그 수정 성공")
	void updateTagByTagId_Success() {
		// given
		Long tagId = 1L;
		TagUpdateRequest request = new TagUpdateRequest("new-name");

		Tag existingTag = new Tag("old-name");
		ReflectionTestUtils.setField(existingTag, "tagId", tagId);

		given(tagRepository.findByTagId(tagId)).willReturn(Optional.of(existingTag));
		given(tagRepository.save(existingTag)).willReturn(existingTag);

		// when
		TagUpdateResponse result = tagService.updateTagByTagId(tagId, request);

		// then
		assertThat(result).isNotNull();
		Tag updatedTag = result.tag();
		assertThat(updatedTag.getTagId()).isEqualTo(tagId);
		assertThat(updatedTag.getTagName()).isEqualTo("new-name");

		verify(tagRepository, times(1)).findByTagId(tagId);
		verify(tagRepository, times(1)).save(existingTag);
	}

	@Test
	@DisplayName("태그 수정 실패 - 존재하지 않는 태그")
	void updateTagByTagId_Fail_TagNotFound() {
		// given
		Long tagId = 999L;
		TagUpdateRequest request = new TagUpdateRequest("new-name");

		given(tagRepository.findByTagId(tagId)).willReturn(Optional.empty());

		// when & then
		assertThatThrownBy(() -> tagService.updateTagByTagId(tagId, request))
			.isInstanceOf(TagNotFoundException.class)
			.hasMessageContaining("해당 아이디 태그를 찾을 수 없습니다");

		verify(tagRepository, times(1)).findByTagId(tagId);
		verify(tagRepository, never()).save(any(Tag.class));
	}

	// ───────────────────────── getAllTags ─────────────────────────

	@Test
	@DisplayName("태그 전체 조회 - 페이징 성공")
	void getAllTags_Success() {
		// given
		Pageable pageable = PageRequest.of(0, 10);

		Tag tag1 = new Tag("backend");
		ReflectionTestUtils.setField(tag1, "tagId", 1L);

		Tag tag2 = new Tag("frontend");
		ReflectionTestUtils.setField(tag2, "tagId", 2L);

		Page<Tag> tagPage = new PageImpl<>(List.of(tag1, tag2), pageable, 2);

		given(tagRepository.findAll(pageable)).willReturn(tagPage);

		// when
		Page<AllTagsInfoResponse> result = tagService.getAllTags(pageable);

		// then
		assertThat(result).isNotNull();
		assertThat(result.getContent()).hasSize(2);

		AllTagsInfoResponse first = result.getContent().get(0);
		AllTagsInfoResponse second = result.getContent().get(1);

		// AllTagsInfoResponse(Tag tag)라고 가정
		assertThat(first.tagId()).isEqualTo(1L);
		assertThat(first.tagName()).isEqualTo("backend");
		assertThat(second.tagId()).isEqualTo(2L);
		assertThat(second.tagName()).isEqualTo("frontend");

		verify(tagRepository, times(1)).findAll(pageable);
	}

	@Test
	@DisplayName("태그 전체 조회 - 빈 페이지")
	void getAllTags_Empty() {
		// given
		Pageable pageable = PageRequest.of(0, 10);
		Page<Tag> emptyPage = Page.empty(pageable);

		given(tagRepository.findAll(pageable)).willReturn(emptyPage);

		// when
		Page<AllTagsInfoResponse> result = tagService.getAllTags(pageable);

		// then
		assertThat(result).isNotNull();
		assertThat(result.getContent()).isEmpty();
		verify(tagRepository, times(1)).findAll(pageable);
	}

	@Test
	@DisplayName("태그 수정 실패 - 이미 존재하는 이름")
	void updateTagByTagId_Fail_TagAlreadyExists() {
		// given
		Long tagId = 1L;
		TagUpdateRequest request = new TagUpdateRequest("duplicate-name");

		Tag existingTag = new Tag("old-name");
		ReflectionTestUtils.setField(existingTag, "tagId", tagId);

		given(tagRepository.findByTagId(tagId)).willReturn(Optional.of(existingTag));
		// 새 이름이 이미 존재한다고 가정 (count != 0)
		given(tagRepository.findTagByTagName("duplicate-name")).willReturn(1L);

		// when & then
		assertThatThrownBy(() -> tagService.updateTagByTagId(tagId, request))
			.isInstanceOf(TagAlreadyExistsException.class)
			.hasMessageContaining("Tag가 이미 존재합니다");

		verify(tagRepository, times(1)).findByTagId(tagId);
		verify(tagRepository, times(1)).findTagByTagName("duplicate-name");
		verify(tagRepository, never()).save(any(Tag.class));
	}

}
