package com.nhnacademy.byeol23backend.image.impl;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import com.nhnacademy.byeol23backend.image.ImageDomain;
import com.nhnacademy.byeol23backend.image.ImageServiceGate;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@SpringBootTest
public class ImageServiceGateMVCTest {
	@Autowired
	ImageServiceGate imageServiceGate;

	@Test
	void contextLoads() {
		assert imageServiceGate != null;
	}

	@Test
	void saveTest(){
		String sampleUrl = "http://example.com/image.jpg";
		Long sampleId = 1L;
		for (ImageDomain domain : ImageDomain.values()) {
			String result = imageServiceGate.saveImageUrl(sampleId, sampleUrl, domain);
			log.info("Saved URL for domain {}: {}", domain, result);
			assert result != null && !result.isEmpty();
		}
	}

	@Test
	void getTest(){
		Long sampleId = 1L;
		for (ImageDomain domain : ImageDomain.values()) {
			List<String> urls = imageServiceGate.getImageUrlsById(sampleId, domain);
			log.info("Retrieved URLs for domain {}: {}", domain, urls);
			assert urls != null;
		}
	}

}
