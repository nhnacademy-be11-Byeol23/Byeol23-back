package com.nhnacademy.byeol23backend.bookset.bookcontributor.service.impl;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.nhnacademy.byeol23backend.bookset.book.domain.Book;
import com.nhnacademy.byeol23backend.bookset.bookcontributor.domain.BookContributor;
import com.nhnacademy.byeol23backend.bookset.bookcontributor.repository.BookContributorRepository;
import com.nhnacademy.byeol23backend.bookset.bookcontributor.service.BookContributorService;
import com.nhnacademy.byeol23backend.bookset.contributor.domain.Contributor;
import com.nhnacademy.byeol23backend.bookset.contributor.repository.ContributorRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class BookContributorServiceImpl implements BookContributorService {
	private final BookContributorRepository bookContributorRepository;
	private final ContributorRepository contributorRepository;

	@Override
	public List<Contributor> getContributorsByBookId(Long bookId) {
		return bookContributorRepository.findContributorByBookId(bookId);
	}

	@Override
	@Transactional
	public void createBookContributors(Book book, List<Long> contributorIds) {
		if (contributorIds.isEmpty()) {
			return;
		}
		List<Contributor> contributors = contributorRepository.findAllById(contributorIds);

		List<BookContributor> bookContributors = contributors.stream()
			.map(contributor -> BookContributor.of(book, contributor))
			.toList();

		bookContributorRepository.saveAll(bookContributors);
		log.info("기여자 추가 완료");
	}

	@Override
	@Transactional
	public void updateBookContributors(Book book, List<Long> contributorIds) {
		List<Contributor> oldContributors = getContributorsByBookId(book.getBookId());
		List<Long> oldContributorIds = oldContributors.stream()
			.map(Contributor::getContributorId)
			.toList();

		List<Long> contributorIdsToAdd = contributorIds.stream()
			.filter(id -> !oldContributorIds.contains(id))
			.toList();
		log.info(contributorIdsToAdd.toString());
		List<Long> contributorIdsToDelete = oldContributorIds.stream()
			.filter(id -> !contributorIds.contains(id))
			.toList();
		if (contributorIdsToAdd.isEmpty() && contributorIdsToDelete.isEmpty()) {
			return;
		}
		log.info(contributorIdsToDelete.toString());

		if (!contributorIdsToAdd.isEmpty()) {
			List<Contributor> contributors = contributorRepository.findAllById(contributorIds);
			List<BookContributor> bookContributors = contributors.stream()
				.map(contributor -> BookContributor.of(book, contributor))
				.toList();
			bookContributorRepository.saveAll(bookContributors);
		}
		if (!contributorIdsToDelete.isEmpty()) {
			bookContributorRepository.deleteByBookIdAndContributorIds(book.getBookId(), contributorIdsToDelete);
		}

		log.info("기여자 업데이트 완료");
	}
}
