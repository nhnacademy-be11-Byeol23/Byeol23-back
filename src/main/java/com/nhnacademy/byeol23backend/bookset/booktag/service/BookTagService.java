package com.nhnacademy.byeol23backend.bookset.booktag.service;

import java.util.List;

import com.nhnacademy.byeol23backend.bookset.book.domain.Book;
import com.nhnacademy.byeol23backend.bookset.tag.domain.Tag;

public interface BookTagService {
	List<Tag> getTagsByBookId(Long bookId);

	void createBookTags(Book book, List<Long> tagIds);

	void updateBookTags(Book book, List<Long> tagIds);

}
