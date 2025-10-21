package com.nhnacademy.byeol23backend.bookset.contributor.service.impl;

import com.nhnacademy.byeol23backend.bookset.contributor.domain.Contributor;
import com.nhnacademy.byeol23backend.bookset.contributor.domain.dto.ContributorCreateRequest;
import com.nhnacademy.byeol23backend.bookset.contributor.domain.dto.ContributorCreateResponse;
import com.nhnacademy.byeol23backend.bookset.contributor.domain.dto.ContributorInfoResponse;
import com.nhnacademy.byeol23backend.bookset.contributor.exception.ContributorNotFound;
import com.nhnacademy.byeol23backend.bookset.contributor.repository.ContributorRepository;
import com.nhnacademy.byeol23backend.bookset.contributor.service.ContributorService;
import org.springframework.stereotype.Service;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

@Service
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class ContributorServiceImpl implements ContributorService {

	private ContributorRepository contributorRepository;

	@Override
	public ContributorInfoResponse getContributorByContributorId(Long contributorId) {
		Contributor contributor = contributorRepository.findById(contributorId)
			.orElseThrow(() -> new ContributorNotFound("There is no such contributor: " + contributorId));
		return new ContributorInfoResponse(contributor);
	}

	@Override
	public ContributorCreateResponse createContributor(ContributorCreateRequest request) {
		Contributor contributor = new Contributor(request);
		contributorRepository.save(contributor);
		return new ContributorCreateResponse(contributor);
	}

	@Override
	public void deleteContributorByContributorId(Long contributorId) {
		contributorRepository.deleteById(contributorId);
	}
}
