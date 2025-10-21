package com.nhnacademy.byeol23backend.bookset.contributor.domain;

import com.nhnacademy.byeol23backend.bookset.contributor.domain.dto.ContributorCreateRequest;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "contributors")
@Getter
public class Contributor {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "contributor_id")
	private Long contributorId;

	@Column(name = "contributor_name", nullable = false, length = 20)
	@Setter
	private String contributorName;

	@Column(name = "contributor_role", nullable = false, length = 10)
	@Setter
	private String contributorRole;

	public Contributor(ContributorCreateRequest contributorCreateRequest){
		this.contributorName = contributorCreateRequest.name();
		this.contributorRole = contributorCreateRequest.role();
	}

}
