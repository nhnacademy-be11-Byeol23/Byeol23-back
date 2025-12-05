package com.nhnacademy.byeol23backend.bookset.contributor.domain.dto;

import com.nhnacademy.byeol23backend.bookset.contributor.domain.Contributor;

public record AllContributorResponse(
	Long contributorId,
	String contributorName,
	String contributorRole
) {
	public AllContributorResponse(Contributor contributor){
		this(contributor.getContributorId(), contributor.getContributorName(), contributor.getContributorRole().getLabel());
	}
}
