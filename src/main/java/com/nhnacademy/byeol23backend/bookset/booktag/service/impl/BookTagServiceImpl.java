package com.nhnacademy.byeol23backend.bookset.booktag.service.impl;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.nhnacademy.byeol23backend.bookset.book.domain.Book;
import com.nhnacademy.byeol23backend.bookset.booktag.domain.BookTag;
import com.nhnacademy.byeol23backend.bookset.booktag.repository.BookTagRepository;
import com.nhnacademy.byeol23backend.bookset.booktag.service.BookTagService;
import com.nhnacademy.byeol23backend.bookset.tag.domain.Tag;
import com.nhnacademy.byeol23backend.bookset.tag.repository.TagRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class BookTagServiceImpl implements BookTagService {
	private final BookTagRepository bookTagRepository;
	private final TagRepository tagRepository;

	@Override
	public List<Tag> getTagsByBookId(Long bookId) {
		return bookTagRepository.findTagsByBookId(bookId);
	}

	@Override
	public void createBookTags(Book book, List<Long> tagIds) {
		if (tagIds.isEmpty()) {
			return;
		}
		List<Tag> tags = tagRepository.findAllById(tagIds);

		List<BookTag> bookTags = tags.stream().map(tag -> BookTag.of(book, tag)).toList();

		bookTagRepository.saveAll(bookTags);
		log.info("태그 추가 완료");
	}

	@Override
	public void updateBookTags(Book book, List<Long> tagIds) {
		bookTagRepository.deleteByBookId(book.getBookId());
		log.info("기존 태그 삭제 완료");
		List<Tag> tags = tagRepository.findAllById(tagIds);
		List<BookTag> bookTags = tags.stream().map(tag -> BookTag.of(book, tag)).toList();
		bookTagRepository.saveAll(bookTags);
		log.info("새 태그 추가 완료");
	}
}
