package com.nhnacademy.byeol23backend.bookset.publisher.controller;

import java.net.URI;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.nhnacademy.byeol23backend.bookset.publisher.domain.dto.AllPublishersInfoResponse;
import com.nhnacademy.byeol23backend.bookset.publisher.domain.dto.PublisherCreateRequest;
import com.nhnacademy.byeol23backend.bookset.publisher.domain.dto.PublisherCreateResponse;
import com.nhnacademy.byeol23backend.bookset.publisher.domain.dto.PublisherInfoResponse;
import com.nhnacademy.byeol23backend.bookset.publisher.domain.dto.PublisherUpdateRequest;
import com.nhnacademy.byeol23backend.bookset.publisher.domain.dto.PublisherUpdateResponse;
import com.nhnacademy.byeol23backend.bookset.publisher.service.PublisherService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/pub")
@RequiredArgsConstructor
public class PublisherController {
	private final PublisherService publisherService;

	@GetMapping("/{publisher-id}")
	public ResponseEntity<PublisherInfoResponse> getPublisherByPublisherId(
		@PathVariable(name = "publisher-id") Long publisherId) {
		PublisherInfoResponse response = publisherService.getPublisherByPublisherId(publisherId);
		return ResponseEntity.ok(response);
	}

	@PostMapping
	public ResponseEntity<PublisherCreateResponse> createPublisher(
		@RequestBody PublisherCreateRequest publisherCreateRequest) {
		PublisherCreateResponse response = publisherService.createPublisher(publisherCreateRequest);
		URI uri = URI.create("/api/pub/" + publisherCreateRequest.publisherName());
		return ResponseEntity.created(uri).body(response);
	}

	@PostMapping("/delete/{publisher-id}")
	public ResponseEntity<Void> deletePublisherByPublisherId(@PathVariable(name = "publisher-id") Long publisherId) {
		publisherService.deletePublisherByPublisherId(publisherId);
		return ResponseEntity.noContent().build();
	}

	@PostMapping("/put/{publisher-id}")
	public ResponseEntity<PublisherUpdateResponse> updatePublisherByPublisherId(
		@PathVariable(name = "publisher-id") Long publisherId,
		@RequestBody PublisherUpdateRequest publisherRequestDto) {
		PublisherUpdateResponse response = publisherService.updatePublisherByPublisherId(publisherId,
			publisherRequestDto);
		return ResponseEntity.ok(response);
	}

	@GetMapping
	public ResponseEntity<Page<AllPublishersInfoResponse>> getAllPublishers(
		@RequestParam(value = "page", defaultValue = "0") int page,
		@RequestParam(value = "size", defaultValue = "20") int size
	) {
		Pageable pageable = PageRequest.of(page, size);
		return ResponseEntity.ok().body(publisherService.getAllPublishers(pageable));
	}

	@GetMapping(value = "/search")
	public ResponseEntity<AllPublishersInfoResponse> findPublisherByName(@RequestParam String publisherName) {
		Optional<AllPublishersInfoResponse> publisher = publisherService.findPublisherByName(publisherName);
		return publisher
			.map(ResponseEntity::ok)
			.orElse(ResponseEntity.notFound().build());
	}

	@PostMapping(value = "/find-or-create")
	public ResponseEntity<AllPublishersInfoResponse> findOrCreatePublisher(
		@RequestBody PublisherCreateRequest request) {
		AllPublishersInfoResponse response = publisherService.findOrCreatePublisher(request.publisherName());
		return ResponseEntity.ok(response);
	}
}
