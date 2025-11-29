package com.nhnacademy.byeol23backend.reviewset.review.dto;

public record ReviewRegisterRequest(
	String reviewContent,
	Byte reviewRate,
	String orderNumber,
	Long bookId,
	Boolean withImage
) {}