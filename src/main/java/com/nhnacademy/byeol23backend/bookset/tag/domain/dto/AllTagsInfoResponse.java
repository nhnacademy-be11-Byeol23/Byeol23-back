package com.nhnacademy.byeol23backend.bookset.tag.domain.dto;

import com.nhnacademy.byeol23backend.bookset.tag.domain.Tag;

public record AllTagsInfoResponse(
	Long tagId,
	String tagName
) {
	public AllTagsInfoResponse(Tag tag){
		this(tag.getTagId(), tag.getTagName());
	}
}
