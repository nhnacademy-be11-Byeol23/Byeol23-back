package com.nhnacademy.byeol23backend.bookset.contributor.domain.dto;

import com.nhnacademy.byeol23backend.bookset.contributor.domain.ContributorRole;

public record ContributorFindOrCreateRequest(
	String contributorName,
	ContributorRole contributorRole
) {
}
