package com.nhnacademy.byeol23backend.bookset.bookcategory.service.impl;

import java.util.List;
import java.util.Objects;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.nhnacademy.byeol23backend.bookset.book.domain.Book;
import com.nhnacademy.byeol23backend.bookset.bookcategory.domain.BookCategory;
import com.nhnacademy.byeol23backend.bookset.bookcategory.repository.BookCategoryRepository;
import com.nhnacademy.byeol23backend.bookset.bookcategory.service.BookCategoryService;
import com.nhnacademy.byeol23backend.bookset.category.domain.Category;
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
		validateCategoryIds(categoryIds);
		List<Category> categories = categoryRepository.findAllById(categoryIds);

		List<BookCategory> bookCategories = categories.stream()
			.map(category -> BookCategory.of(book, category))
			.toList();

		bookCategoryRepository.saveAll(bookCategories);
		log.info("카테고리 추가 완료");
	}

	@Override
	@Transactional
	public void updateBookCategories(Book book, List<Long> categoryIds) {
		validateCategoryIds(categoryIds);
		List<Category> oldCategories = getCategoriesByBookId(book.getBookId());
		List<Long> oldCategoryIds = oldCategories.stream().map(Category::getCategoryId).toList();

		List<Long> categoryIdsToAdd = categoryIds.stream()
			.filter(id -> !oldCategoryIds.contains(id))
			.toList();
		
		List<Long> categoryIdsToDelete = oldCategoryIds.stream()
			.filter(id -> !categoryIds.contains(id))
			.toList();

		log.info("추가할 카테고리 ID: {}", categoryIdsToAdd);
		log.info("삭제할 카테고리 ID: {}", categoryIdsToDelete);

		if (categoryIdsToAdd.isEmpty() && categoryIdsToDelete.isEmpty()) {
			log.info("카테고리 변경사항 없음");
			return;
		}

		if (!categoryIdsToAdd.isEmpty()) {
			List<Category> categoriesToAdd = categoryRepository.findAllById(categoryIdsToAdd);

			List<BookCategory> bookCategories = categoriesToAdd.stream()
				.map(category -> BookCategory.of(book, category))
				.toList();

			bookCategoryRepository.saveAll(bookCategories);
			log.info("카테고리 {}개 추가 완료", categoryIdsToAdd.size());
		}
		
		if (!categoryIdsToDelete.isEmpty()) {
			bookCategoryRepository.deleteByBookIdAndCategoryIds(book.getBookId(), categoryIdsToDelete);
			log.info("카테고리 {}개 삭제 완료", categoryIdsToDelete.size());
		}
		
		log.info("카테고리 수정 완료");
	}

	private void validateCategoryIds(List<Long> categoryIds) {
		if (categoryIds == null || categoryIds.isEmpty()) {
			throw new IllegalArgumentException("카테고리는 최소 한 개 이상 선택해야 합니다.");
		}
		if (categoryIds.stream().anyMatch(Objects::isNull)) {
			throw new IllegalArgumentException("카테고리 ID에 null 값이 포함되어 있습니다.");
		}
	}
}
