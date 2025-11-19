package com.nhnacademy.byeol23backend.bookset.contributor.controller;

import java.net.URI;

import org.springframework.data.domain.Page;
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

import com.nhnacademy.byeol23backend.bookset.contributor.domain.dto.AllContributorResponse;
import com.nhnacademy.byeol23backend.bookset.contributor.domain.dto.ContributorCreateRequest;
import com.nhnacademy.byeol23backend.bookset.contributor.domain.dto.ContributorCreateResponse;
import com.nhnacademy.byeol23backend.bookset.contributor.domain.dto.ContributorFindOrCreateRequest;
import com.nhnacademy.byeol23backend.bookset.contributor.domain.dto.ContributorInfoResponse;
import com.nhnacademy.byeol23backend.bookset.contributor.domain.dto.ContributorUpdateRequest;
import com.nhnacademy.byeol23backend.bookset.contributor.domain.dto.ContributorUpdateResponse;
import com.nhnacademy.byeol23backend.bookset.contributor.service.ContributorService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/contributors")
@RequiredArgsConstructor
public class ContributorController {
	private final ContributorService contributorService;

	@GetMapping("/{contributor-id}")
	public ResponseEntity<ContributorInfoResponse> getContributorByContributorId(@PathVariable(name = "contributor-id") Long contributorId){
		ContributorInfoResponse response = contributorService.getContributorByContributorId(contributorId);
		return ResponseEntity.ok().body(response);
	}

	@PostMapping
	public ResponseEntity<ContributorCreateResponse> createContributor(@RequestBody ContributorCreateRequest request){
		ContributorCreateResponse response = contributorService.createContributor(request);
		URI uri = URI.create("/api/contributors/" + response.contributor().getContributorId());
		return ResponseEntity.created(uri).body(response);
	}

	@PostMapping("/find-or-create")
	public ResponseEntity<AllContributorResponse> findOrCreateContributor(
	    @RequestBody ContributorFindOrCreateRequest request) {
	    Long contributorId = contributorService.findOrCreateContributor(
	        request.contributorName(), 
	        request.contributorRole()
	    );
	    ContributorInfoResponse infoResponse = contributorService.getContributorByContributorId(contributorId);
	    return ResponseEntity.ok(new AllContributorResponse(infoResponse.contributor()));
	}

	@DeleteMapping("/{contributor-id}")
	public ResponseEntity<Void> deleteContributorByContributorId(@PathVariable(name = "contributor-id") Long contributorId){
		contributorService.deleteContributorByContributorId(contributorId);
		return ResponseEntity.noContent().build();
	}

	@PutMapping("/{contributor-id}")
	public ResponseEntity<ContributorUpdateResponse> updateContributor(@PathVariable(name = "contributor-id") Long contributorId, @RequestBody ContributorUpdateRequest request){
		ContributorUpdateResponse response = contributorService.updateContributor(contributorId, request);
		return ResponseEntity.ok(response);
	}

	@GetMapping
	public ResponseEntity<Page<AllContributorResponse>> getAllContributors(@RequestParam(value = "page") int page, @RequestParam(value = "size") int size){
		Page<AllContributorResponse> contributors = contributorService.getAllContributors(page, size);
		return ResponseEntity.ok(contributors);
	}
}
