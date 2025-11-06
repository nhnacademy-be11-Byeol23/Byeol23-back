package com.nhnacademy.byeol23backend.bookset.publisher.service;

import com.nhnacademy.byeol23backend.bookset.publisher.domain.dto.PublisherCreateRequest;
import com.nhnacademy.byeol23backend.bookset.publisher.domain.dto.PublisherCreateResponse;
import com.nhnacademy.byeol23backend.bookset.publisher.domain.dto.PublisherInfoResponse;
import com.nhnacademy.byeol23backend.bookset.publisher.domain.dto.PublisherUpdateRequest;
import com.nhnacademy.byeol23backend.bookset.publisher.domain.dto.PublisherUpdateResponse;

public interface PublisherService {
	PublisherCreateResponse createPublisher(PublisherCreateRequest request);
	PublisherInfoResponse getPublisherByPublisherId(Long publisherId);
	void deletePublisherByPublisherId(Long publisherId);
	PublisherUpdateResponse updatePublisherByPublisherId(Long publisherId, PublisherUpdateRequest request);
}
