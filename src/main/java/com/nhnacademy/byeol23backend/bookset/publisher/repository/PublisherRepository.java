package com.nhnacademy.byeol23backend.bookset.publisher.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.nhnacademy.byeol23backend.bookset.publisher.domain.Publisher;

@Repository
public interface PublisherRepository extends JpaRepository<Publisher, Long> {
	Publisher getPublisherByPublisherId(Long publisherId);

	void deletePublisherByPublisherId(Long publisherId);

	Publisher findPublisherByPublisherId(Long publisherId);

	Optional<Publisher> findByPublisherId(Long publisherId);

	Optional<Publisher> findByPublisherName(String publisherName);
}
