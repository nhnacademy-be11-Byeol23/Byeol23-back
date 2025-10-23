package com.nhnacademy.byeol23backend.bookset.tag.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.nhnacademy.byeol23backend.bookset.tag.domain.dto.AllTagsInfoResponse;
import com.nhnacademy.byeol23backend.bookset.tag.domain.dto.TagCreateRequest;
import com.nhnacademy.byeol23backend.bookset.tag.domain.dto.TagCreateResponse;
import com.nhnacademy.byeol23backend.bookset.tag.domain.dto.TagInfoResponse;
import com.nhnacademy.byeol23backend.bookset.tag.domain.dto.TagUpdateRequest;
import com.nhnacademy.byeol23backend.bookset.tag.domain.dto.TagUpdateResponse;

public interface TagService {
	TagCreateResponse createTag(TagCreateRequest request);
	TagInfoResponse getTagByTagId(Long tagId);
	void deleteTagByTagId(Long tagId);
	TagUpdateResponse updateTagByTagId(Long tagId, TagUpdateRequest request);
	Page<AllTagsInfoResponse> getAllTags(Pageable pageable);
}
