package com.nhnacademy.byeol23backend.bookset.contributor.service.impl;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.nhnacademy.byeol23backend.bookset.contributor.domain.Contributor;
import com.nhnacademy.byeol23backend.bookset.contributor.domain.ContributorRole;
import com.nhnacademy.byeol23backend.bookset.contributor.domain.dto.AllContributorResponse;
import com.nhnacademy.byeol23backend.bookset.contributor.domain.dto.ContributorCreateRequest;
import com.nhnacademy.byeol23backend.bookset.contributor.domain.dto.ContributorCreateResponse;
import com.nhnacademy.byeol23backend.bookset.contributor.domain.dto.ContributorInfoResponse;
import com.nhnacademy.byeol23backend.bookset.contributor.domain.dto.ContributorUpdateRequest;
import com.nhnacademy.byeol23backend.bookset.contributor.domain.dto.ContributorUpdateResponse;
import com.nhnacademy.byeol23backend.bookset.contributor.exception.ContributorNotFound;
import com.nhnacademy.byeol23backend.bookset.contributor.repository.ContributorRepository;
import com.nhnacademy.byeol23backend.bookset.contributor.service.ContributorService;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ContributorServiceImpl implements ContributorService {

	private final ContributorRepository contributorRepository;

	@Override
	public ContributorInfoResponse getContributorByContributorId(Long contributorId) {
		Contributor contributor = contributorRepository.findById(contributorId)
			.orElseThrow(() -> new ContributorNotFound("해당 기여자 없음: " + contributorId));
		return new ContributorInfoResponse(contributor);
	}

	@Override
	public ContributorCreateResponse createContributor(ContributorCreateRequest request) {
		if (request == null)
			throw new IllegalArgumentException("request is null");
		if (request.contributorName() == null || request.contributorName().isBlank()) {
			throw new IllegalArgumentException("name is required");
		}
		if (request.contributorRole() == null) {
			throw new IllegalArgumentException("contributorRole is required");
		}

		Contributor contributor = new Contributor(request);
		contributorRepository.save(contributor);
		return new ContributorCreateResponse(contributor);
	}

	@Override
	public void deleteContributorByContributorId(Long contributorId) {
		contributorRepository.deleteById(contributorId);
	}

	@Override
	@Transactional
	public ContributorUpdateResponse updateContributor(Long contributorId, ContributorUpdateRequest request) {
		if (request == null)
			throw new IllegalArgumentException("request is null");
		if (request.contributorName() == null || request.contributorName().isBlank()) {
			throw new IllegalArgumentException("name is required");
		}
		if (request.contributorRole() == null) {
			throw new IllegalArgumentException("contributorRole is required");
		}

		Contributor contributor = contributorRepository.findById(contributorId)
			.orElseThrow(() -> new ContributorNotFound("해당 기여자 없음: " + contributorId));
		contributor.setContributorName(request.contributorName());
		contributor.setContributorRole(request.contributorRole());
		return new ContributorUpdateResponse(contributor);
	}

	@Override
	public Page<AllContributorResponse> getAllContributors(int page, int size) {
		Pageable pageable = PageRequest.of(page, size);
		return contributorRepository.findAll(pageable).map(AllContributorResponse::new);
	}

	@Override
	@Transactional
	public Long findOrCreateContributor(String contributorName, ContributorRole contributorRole) {
		if (contributorName == null || contributorName.isBlank()) {
			throw new IllegalArgumentException("contributorName은 null일 수 없다.");
		}
		// 이름과 역할로 기여자 찾기
		return contributorRepository
			.findByContributorNameAndContributorRole(contributorName, contributorRole)
			.map(Contributor::getContributorId)
			.orElseGet(() -> {
				ContributorCreateRequest request = new ContributorCreateRequest(
					contributorName,
					contributorRole
				);
				Contributor contributor = new Contributor(request);
				Contributor saved = contributorRepository.save(contributor);
				return saved.getContributorId();
			});
	}
}
