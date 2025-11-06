package com.nhnacademy.byeol23backend.bookset.publisher.controller;

import java.net.URI;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.nhnacademy.byeol23backend.bookset.publisher.domain.dto.PublisherCreateRequest;
import com.nhnacademy.byeol23backend.bookset.publisher.domain.dto.PublisherCreateResponse;
import com.nhnacademy.byeol23backend.bookset.publisher.domain.dto.PublisherInfoResponse;
import com.nhnacademy.byeol23backend.bookset.publisher.domain.dto.PublisherUpdateRequest;
import com.nhnacademy.byeol23backend.bookset.publisher.domain.dto.PublisherUpdateResponse;
import com.nhnacademy.byeol23backend.bookset.publisher.service.PublisherService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/publishers")
@RequiredArgsConstructor
public class PublisherController {
	private final PublisherService publisherService;

	@GetMapping("/{publisherId}")
	public ResponseEntity<PublisherInfoResponse> getPublisherByPublisherId(@PathVariable Long publisherId){
		PublisherInfoResponse response = publisherService.getPublisherByPublisherId(publisherId);
		return ResponseEntity.ok(response);
	}



	@PostMapping
	public ResponseEntity<PublisherCreateResponse> createPublisher(@RequestBody PublisherCreateRequest publisherCreateRequest){
		PublisherCreateResponse response = publisherService.createPublisher(publisherCreateRequest);
		URI uri = URI.create("/api/publishers/" + publisherCreateRequest.publisherName());
		return ResponseEntity.created(uri).body(response);
	}


	@DeleteMapping("/{publisherId}")
	public ResponseEntity<Void> deletePublisherByPublisherId(@PathVariable Long publisherId) {
		publisherService.deletePublisherByPublisherId(publisherId);
		return ResponseEntity.noContent().build();
	}

	@PutMapping("/{publisherId}")
	public ResponseEntity<PublisherUpdateResponse> updatePublisherByPublisherId(@PathVariable Long publisherId, @RequestBody PublisherUpdateRequest publisherRequestDto) {
		PublisherUpdateResponse response = publisherService.updatePublisherByPublisherId(publisherId, publisherRequestDto);
		return ResponseEntity.ok(response);
	}
}
