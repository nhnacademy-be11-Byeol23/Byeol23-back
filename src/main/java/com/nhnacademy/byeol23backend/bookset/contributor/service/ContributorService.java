package com.nhnacademy.byeol23backend.bookset.contributor.service;

import org.springframework.data.domain.Page;

import com.nhnacademy.byeol23backend.bookset.contributor.domain.ContributorRole;
import com.nhnacademy.byeol23backend.bookset.contributor.domain.dto.AllContributorResponse;
import com.nhnacademy.byeol23backend.bookset.contributor.domain.dto.ContributorCreateRequest;
import com.nhnacademy.byeol23backend.bookset.contributor.domain.dto.ContributorCreateResponse;
import com.nhnacademy.byeol23backend.bookset.contributor.domain.dto.ContributorInfoResponse;
import com.nhnacademy.byeol23backend.bookset.contributor.domain.dto.ContributorUpdateRequest;
import com.nhnacademy.byeol23backend.bookset.contributor.domain.dto.ContributorUpdateResponse;

public interface ContributorService {
	public ContributorInfoResponse getContributorByContributorId(Long contributorId);

	public ContributorCreateResponse createContributor(ContributorCreateRequest request);

	public void deleteContributorByContributorId(Long contributorId);

	public ContributorUpdateResponse updateContributor(Long contributorId, ContributorUpdateRequest request);

	public Page<AllContributorResponse> getAllContributors(int page, int size);

	Long findOrCreateContributor(String contributorName, ContributorRole contributorRole);
}
