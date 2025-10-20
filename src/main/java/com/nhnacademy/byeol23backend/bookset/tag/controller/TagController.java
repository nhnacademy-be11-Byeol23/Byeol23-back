package com.nhnacademy.byeol23backend.bookset.tag.controller;

import java.net.URI;
import java.util.Objects;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.nhnacademy.byeol23backend.bookset.tag.domain.Tag;
import com.nhnacademy.byeol23backend.bookset.tag.domain.dto.TagCreateRequest;
import com.nhnacademy.byeol23backend.bookset.tag.domain.dto.TagCreateResponse;
import com.nhnacademy.byeol23backend.bookset.tag.domain.dto.TagDeleteResponse;
import com.nhnacademy.byeol23backend.bookset.tag.domain.dto.TagInfoResponse;
import com.nhnacademy.byeol23backend.bookset.tag.domain.dto.TagUpdateRequest;
import com.nhnacademy.byeol23backend.bookset.tag.domain.dto.TagUpdateResponse;
import com.nhnacademy.byeol23backend.bookset.tag.exception.NoSuchTagException;
import com.nhnacademy.byeol23backend.bookset.tag.exception.TagPostFormatException;
import com.nhnacademy.byeol23backend.bookset.tag.repository.TagRepository;
import com.nhnacademy.byeol23backend.bookset.tag.service.TagService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/tags")
@RequiredArgsConstructor
public class TagController {
	private final TagService tagService;

	@GetMapping("/{tagId}")
	public ResponseEntity<TagInfoResponse> infoTag(@PathVariable Long tagId){
		TagInfoResponse response = tagService.getTag(tagId);
		return ResponseEntity.ok(response);
	}



	@PostMapping
	public ResponseEntity<TagCreateResponse> createTag(@RequestBody TagCreateRequest tagCreateRequest){
		TagCreateResponse response = tagService.createTag(tagCreateRequest);
		URI uri = URI.create("/api/tags/" + tagCreateRequest.tagName());
		return ResponseEntity.created(uri).body(response);
	}


	@DeleteMapping("/{tagId}")
	public ResponseEntity<Void> deleteTag(@PathVariable Long tagId) {
		tagService.deleteTag(tagId);
		return ResponseEntity.noContent().build();
	}

	@PutMapping("/{tagId}")
	public ResponseEntity<TagUpdateResponse> updateTag(@PathVariable Long tagId, @RequestBody TagUpdateRequest tagRequestDto) {
		TagUpdateResponse response = tagService.updateTag(tagId, tagRequestDto);
		return ResponseEntity.ok(response);
	}
}
