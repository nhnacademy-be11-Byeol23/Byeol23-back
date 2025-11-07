package com.nhnacademy.byeol23backend.bookset.bookcategory.service;

import java.util.List;

import com.nhnacademy.byeol23backend.bookset.book.domain.Book;
import com.nhnacademy.byeol23backend.bookset.category.domain.Category;

public interface BookCategoryService {
	List<Category> getCategoriesByBookId(Long bookId);

	void createBookCategories(Book book, List<Long> categoryIds);

	void updateBookCategories(Book book, List<Long> categoryIds);
}
