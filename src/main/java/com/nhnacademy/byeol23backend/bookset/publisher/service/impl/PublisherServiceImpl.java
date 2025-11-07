package com.nhnacademy.byeol23backend.bookset.publisher.service.impl;

import com.nhnacademy.byeol23backend.bookset.publisher.service.PublisherService;
import com.nhnacademy.byeol23backend.bookset.publisher.domain.Publisher;
import com.nhnacademy.byeol23backend.bookset.publisher.domain.Publisher;
import com.nhnacademy.byeol23backend.bookset.publisher.domain.dto.PublisherCreateRequest;
import com.nhnacademy.byeol23backend.bookset.publisher.domain.dto.PublisherCreateResponse;
import com.nhnacademy.byeol23backend.bookset.publisher.domain.dto.PublisherInfoResponse;
import com.nhnacademy.byeol23backend.bookset.publisher.domain.dto.PublisherUpdateRequest;
import com.nhnacademy.byeol23backend.bookset.publisher.domain.dto.PublisherUpdateResponse;
import com.nhnacademy.byeol23backend.bookset.publisher.exception.PublisherNotFoundException;
import com.nhnacademy.byeol23backend.bookset.publisher.repository.PublisherRepository;
import com.nhnacademy.byeol23backend.bookset.publisher.service.PublisherService;
import com.nhnacademy.byeol23backend.bookset.publisher.domain.dto.AllPublishersInfoResponse;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class PublisherServiceImpl implements PublisherService {
	private final PublisherRepository publisherRepository;

	@Override
	public PublisherCreateResponse createPublisher(PublisherCreateRequest request) {
		Publisher publisher = new Publisher(request.publisherName());
		publisherRepository.save(publisher);
		return new PublisherCreateResponse(publisher);
	}

	@Override
	public PublisherInfoResponse getPublisherByPublisherId(Long publisherId) {
		Publisher publisher = publisherRepository.getPublisherByPublisherId(publisherId);
		return new PublisherInfoResponse(publisher);
	}

	@Override
	@Transactional
	public void deletePublisherByPublisherId(Long publisherId) {
		Publisher publisher = publisherRepository.findByPublisherId(publisherId)
			.orElseThrow(() -> new PublisherNotFoundException("해당 아이디 태그를 찾을 수 없습니다: " + publisherId));
		publisherRepository.deletePublisherByPublisherId(publisherId);
	}

	@Override
	public PublisherUpdateResponse updatePublisherByPublisherId(Long publisherId, PublisherUpdateRequest request) {
		Publisher publisher = publisherRepository.findByPublisherId(publisherId)
			.orElseThrow(() -> new PublisherNotFoundException("해당 아이디 태그를 찾을 수 없습니다: " + publisherId));
		publisher.setPublisherName(request.publisherName());
		publisherRepository.save(publisher);
		return new PublisherUpdateResponse(publisher);
	}

	@Override
	public Page<AllPublishersInfoResponse> getAllPublishers(Pageable pageable) {
		return publisherRepository.findAll(pageable).map(AllPublishersInfoResponse::new);
	}
}