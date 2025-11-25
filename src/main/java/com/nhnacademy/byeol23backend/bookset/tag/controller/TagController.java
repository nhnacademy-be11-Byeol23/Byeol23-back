package com.nhnacademy.byeol23backend.bookset.tag.controller;

import java.net.URI;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.nhnacademy.byeol23backend.bookset.tag.domain.dto.AllTagsInfoResponse;
import com.nhnacademy.byeol23backend.bookset.tag.domain.dto.TagCreateRequest;
import com.nhnacademy.byeol23backend.bookset.tag.domain.dto.TagCreateResponse;
import com.nhnacademy.byeol23backend.bookset.tag.domain.dto.TagInfoResponse;
import com.nhnacademy.byeol23backend.bookset.tag.domain.dto.TagUpdateRequest;
import com.nhnacademy.byeol23backend.bookset.tag.domain.dto.TagUpdateResponse;
import com.nhnacademy.byeol23backend.bookset.tag.service.TagService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/tags")
@RequiredArgsConstructor
public class TagController {
	private final TagService tagService;

	@GetMapping("/{tag-id}")
	public ResponseEntity<TagInfoResponse> getTagByTagId(@PathVariable(name = "tag-id") Long tagId){
		TagInfoResponse response = tagService.getTagByTagId(tagId);
		return ResponseEntity.ok(response);
	}



	@PostMapping
	public ResponseEntity<TagCreateResponse> createTag(@RequestBody TagCreateRequest tagCreateRequest){
		TagCreateResponse response = tagService.createTag(tagCreateRequest);
		URI uri = URI.create("/api/tags/" + tagCreateRequest.tagName());
		return ResponseEntity.created(uri).body(response);
	}


	@PostMapping("/delete/{tag-id}")
	public ResponseEntity<Void> deleteTagByTagId(@PathVariable(name = "tag-id") Long tagId) {
		tagService.deleteTagByTagId(tagId);
		return ResponseEntity.noContent().build();
	}

	@PostMapping("/put/{tag-id}")
	public ResponseEntity<TagUpdateResponse> updateTagByTagId(@PathVariable(name = "tag-id") Long tagId, @RequestBody TagUpdateRequest tagRequestDto) {
		TagUpdateResponse response = tagService.updateTagByTagId(tagId, tagRequestDto);
		return ResponseEntity.ok(response);
	}

	@GetMapping
	public ResponseEntity<Page<AllTagsInfoResponse>> getAllTags(
		@RequestParam(value = "page", defaultValue = "0") int page,
		@RequestParam(value = "size", defaultValue = "10") int size
	){
		Pageable pageable = PageRequest.of(page, size);
		return ResponseEntity.ok().body(tagService.getAllTags(pageable));
	}
}
