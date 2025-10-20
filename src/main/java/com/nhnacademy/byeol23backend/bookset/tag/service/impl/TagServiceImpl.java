package com.nhnacademy.byeol23backend.bookset.tag.service.impl;

import com.nhnacademy.byeol23backend.bookset.tag.domain.Tag;
import com.nhnacademy.byeol23backend.bookset.tag.domain.dto.TagCreateRequest;
import com.nhnacademy.byeol23backend.bookset.tag.domain.dto.TagCreateResponse;
import com.nhnacademy.byeol23backend.bookset.tag.domain.dto.TagDeleteResponse;
import com.nhnacademy.byeol23backend.bookset.tag.domain.dto.TagInfoResponse;
import com.nhnacademy.byeol23backend.bookset.tag.domain.dto.TagUpdateRequest;
import com.nhnacademy.byeol23backend.bookset.tag.domain.dto.TagUpdateResponse;
import com.nhnacademy.byeol23backend.bookset.tag.exception.NoSuchTagException;
import com.nhnacademy.byeol23backend.bookset.tag.repository.TagRepository;
import com.nhnacademy.byeol23backend.bookset.tag.service.TagService;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class TagServiceImpl implements TagService {
	private final TagRepository tagRepository;

	@Override
	public TagCreateResponse createTag(TagCreateRequest request) {
		Tag tag = new Tag(request.tagName());
		tagRepository.save(tag);
		return new TagCreateResponse(tag);
	}

	@Override
	public TagInfoResponse getTag(Long tagId) {
		Tag tag = tagRepository.getTagByTagId(tagId);
		return new TagInfoResponse(tag);
	}

	@Override
	public void deleteTag(Long tagId) {
		Tag tag = tagRepository.findByTagId(tagId)
			.orElseThrow(() -> new NoSuchTagException("해당 아이디 태그를 찾을 수 없습니다: " + tagId));
		tagRepository.deleteTagByTagId(tagId);
	}

	@Override
	public TagUpdateResponse updateTag(Long tagId, TagUpdateRequest request) {
		Tag tag = tagRepository.findTagByTagId(tagId);
		tag.setTagName(request.tagName());
		return new TagUpdateResponse(tag);
	}
}