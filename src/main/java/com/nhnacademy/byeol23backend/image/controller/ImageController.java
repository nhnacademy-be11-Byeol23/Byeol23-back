package com.nhnacademy.byeol23backend.image.controller;

import java.util.List;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.nhnacademy.byeol23backend.image.domain.ImageDomain;
import com.nhnacademy.byeol23backend.image.dto.GetUrlResponse;
import com.nhnacademy.byeol23backend.image.dto.ImageDeleteRequest;
import com.nhnacademy.byeol23backend.image.dto.ImageUploadRequest;
import com.nhnacademy.byeol23backend.image.service.ImageServiceGate;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/images")
public class ImageController {
	private final ImageServiceGate imageServiceGate;

	@PostMapping("/upload")
	public ResponseEntity<String> uploadImage(
		@RequestBody ImageUploadRequest imageUploadRequest
	) {
		imageServiceGate.saveImageUrl(
			imageUploadRequest.id(),
			imageUploadRequest.imageUrl(),
			imageUploadRequest.imageDomain()
		);
		return ResponseEntity.ok().body("success");
	}

	@PostMapping("/delete")
	public ResponseEntity<String> deleteImage(
		@RequestBody ImageDeleteRequest imageDeleteRequest
	) {
		String response = imageServiceGate.deleteImageUrlsById(
			imageDeleteRequest.imageId(),
			imageDeleteRequest.imageDomain()
		);
		return ResponseEntity.ok().body(response);
	}

	@GetMapping("/urls/{domain}/{id}")
	public ResponseEntity<List<GetUrlResponse>> getImageUrls(
		@PathVariable ImageDomain domain,
		@PathVariable Long id
	) {
		Map<Long, String> imageUrls = imageServiceGate.getImageUrlsById(id, domain);
		List<GetUrlResponse> response = imageUrls.entrySet().stream()
			.map(entry -> new GetUrlResponse(entry.getKey(), entry.getValue()))
			.toList();
		return ResponseEntity.ok().body(response);
	}
}
