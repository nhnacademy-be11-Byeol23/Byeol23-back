package com.nhnacademy.byeol23backend.bookset.publisher.domain.dto;

import com.nhnacademy.byeol23backend.bookset.publisher.domain.Publisher;

public record AllPublishersInfoResponse(
	Long publisherId,
	String publisherName
) {
	public AllPublishersInfoResponse(Publisher publisher){
		this(publisher.getPublisherId(), publisher.getPublisherName());
	}
}
