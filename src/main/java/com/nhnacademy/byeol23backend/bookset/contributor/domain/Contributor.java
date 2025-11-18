package com.nhnacademy.byeol23backend.bookset.contributor.domain;

import com.nhnacademy.byeol23backend.bookset.contributor.domain.dto.ContributorCreateRequest;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Table(name = "contributors")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class Contributor {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "contributor_id")
	private Long contributorId;

	@Column(name = "contributor_name", nullable = false, length = 20)
	@Setter
	private String contributorName;

	@Enumerated(EnumType.STRING)
	@Column(name = "contributor_role", nullable = false, length = 10)
	private ContributorRole contributorRole;

	public Contributor(ContributorCreateRequest contributorCreateRequest) {
		this.contributorName = contributorCreateRequest.contributorName();
		this.contributorRole = contributorCreateRequest.contributorRole();
	}

	public void setContributorRole(String contributorRole) {
		this.contributorRole = ContributorRole.from(contributorRole);
	}

	public String getContributorRole() {
		return this.contributorRole.getLabel();
	}
}