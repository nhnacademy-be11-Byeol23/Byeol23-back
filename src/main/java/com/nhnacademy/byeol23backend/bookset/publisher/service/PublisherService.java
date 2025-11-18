package com.nhnacademy.byeol23backend.bookset.publisher.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.nhnacademy.byeol23backend.bookset.publisher.domain.dto.AllPublishersInfoResponse;
import com.nhnacademy.byeol23backend.bookset.publisher.domain.dto.PublisherCreateRequest;
import com.nhnacademy.byeol23backend.bookset.publisher.domain.dto.PublisherCreateResponse;
import com.nhnacademy.byeol23backend.bookset.publisher.domain.dto.PublisherInfoResponse;
import com.nhnacademy.byeol23backend.bookset.publisher.domain.dto.PublisherUpdateRequest;
import com.nhnacademy.byeol23backend.bookset.publisher.domain.dto.PublisherUpdateResponse;

import java.util.Optional;

public interface PublisherService {
	PublisherCreateResponse createPublisher(PublisherCreateRequest request);
	PublisherInfoResponse getPublisherByPublisherId(Long publisherId);
	void deletePublisherByPublisherId(Long publisherId);
	PublisherUpdateResponse updatePublisherByPublisherId(Long publisherId, PublisherUpdateRequest request);
	Page<AllPublishersInfoResponse> getAllPublishers(Pageable pageable);
	
	Optional<AllPublishersInfoResponse> findPublisherByName(String publisherName);
	
	AllPublishersInfoResponse findOrCreatePublisher(String publisherName);
}
