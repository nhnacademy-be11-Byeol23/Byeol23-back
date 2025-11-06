package com.nhnacademy.byeol23backend.bookset.bookcategory.service.impl;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.nhnacademy.byeol23backend.bookset.book.domain.Book;
import com.nhnacademy.byeol23backend.bookset.bookcategory.domain.BookCategory;
import com.nhnacademy.byeol23backend.bookset.bookcategory.repository.BookCategoryRepository;
import com.nhnacademy.byeol23backend.bookset.bookcategory.service.BookCategoryService;
import com.nhnacademy.byeol23backend.bookset.category.domain.Category;
import com.nhnacademy.byeol23backend.bookset.category.exception.CategoryNotFoundException;
import com.nhnacademy.byeol23backend.bookset.category.repository.CategoryRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class BookCategoryServiceImpl implements BookCategoryService {

	private final BookCategoryRepository bookCategoryRepository;
	private final CategoryRepository categoryRepository;

	@Override
	public List<Category> getCategoriesByBookId(Long bookId) {
		return bookCategoryRepository.findCategoriesByBookId(bookId);
	}

	@Override
	@Transactional
	public void createBookCategories(Book book, List<Long> categoryIds) {
		if (categoryIds == null || categoryIds.isEmpty()) {
			return;
		}
		log.info("새 카테고리 추가 시작");
		for (Long categoryId : categoryIds) {
			Category category = categoryRepository.findById(categoryId)
				.orElseThrow(() -> new CategoryNotFoundException("존재하지 않는 카테고리 ID입니다: " + categoryId));
			BookCategory bookCategory = BookCategory.of(book, category);
			bookCategoryRepository.save(bookCategory);
		}
		log.info("새 카테고리 추가 완료");
	}

	@Override
	@Transactional
	public void updateBookCategories(Book book, List<Long> categoryIds) {
		// 기존 카테고리를 모두 삭제
		bookCategoryRepository.deleteByBookId(book.getBookId());
		log.info("기존 카테고리 삭제 완료");
		
		if (categoryIds != null && !categoryIds.isEmpty()) {
			for (Long categoryId : categoryIds) {
				Category category = categoryRepository.findById(categoryId)
					.orElseThrow(() -> new CategoryNotFoundException("존재하지 않는 카테고리 ID입니다: " + categoryId));
				BookCategory bookCategory = BookCategory.of(book, category);
				bookCategoryRepository.save(bookCategory);
			}
		}
		log.info("새 카테고리 추가 완료");
	}
}
