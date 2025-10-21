package com.nhnacademy.byeol23backend.bookset.contributor.controller;

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

import com.nhnacademy.byeol23backend.bookset.contributor.domain.dto.ContributorCreateRequest;
import com.nhnacademy.byeol23backend.bookset.contributor.domain.dto.ContributorCreateResponse;
import com.nhnacademy.byeol23backend.bookset.contributor.domain.dto.ContributorInfoResponse;
import com.nhnacademy.byeol23backend.bookset.contributor.domain.dto.ContributorUpdateRequest;
import com.nhnacademy.byeol23backend.bookset.contributor.domain.dto.ContributorUpdateResponse;
import com.nhnacademy.byeol23backend.bookset.contributor.service.ContributorService;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

@RestController
@RequestMapping("/api/contributors")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class ContributorController {
	private ContributorService contributorService;

	@GetMapping("/{contributorId}")
	public ResponseEntity<ContributorInfoResponse> getContributorByContributorId(@PathVariable Long contributorId){
		ContributorInfoResponse response = contributorService.getContributorByContributorId(contributorId);
		return ResponseEntity.ok().body(response);
	}

	@PostMapping
	public ResponseEntity<ContributorCreateResponse> createContributor(@RequestBody ContributorCreateRequest request){
		ContributorCreateResponse response = contributorService.createContributor(request);
		URI uri = URI.create("/api/contributors/" + response.contributor().getContributorId());
		return ResponseEntity.created(uri).body(response);
	}

	@DeleteMapping("/{contributorId}")
	public ResponseEntity<Void> deleteContributorByContributorId(@PathVariable Long contributorId){
		contributorService.deleteContributorByContributorId(contributorId);
		return ResponseEntity.noContent().build();
	}

	@PutMapping("/{contributorId}")
	public ResponseEntity<ContributorUpdateResponse> updateContributorByContributorId(@PathVariable Long contributorId, @RequestBody ContributorUpdateRequest request){
		ContributorUpdateResponse response = contributorService.updateContributor(contributorId, request);
		return ResponseEntity.ok(response);
	}

}
