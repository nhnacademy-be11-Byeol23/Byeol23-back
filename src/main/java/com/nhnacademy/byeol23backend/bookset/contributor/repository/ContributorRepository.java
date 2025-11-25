package com.nhnacademy.byeol23backend.bookset.contributor.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.nhnacademy.byeol23backend.bookset.contributor.domain.Contributor;
import com.nhnacademy.byeol23backend.bookset.contributor.domain.ContributorRole;

public interface ContributorRepository extends JpaRepository<Contributor, Long> {
	Optional<Contributor> findByContributorNameAndContributorRole(String contributorName,
		ContributorRole contributorRole);

	@Query("select count(c) from Contributor c where c.contributorName = :contributorName and c.contributorRole = :contributorRole")
	Long findContributorByNameAndRole(String contributorName, ContributorRole contributorRole);
}
