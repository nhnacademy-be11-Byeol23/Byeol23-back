package com.nhnacademy.byeol23backend.bookset.category.service.impl;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.nhnacademy.byeol23backend.bookset.book.repository.BookRepository;
import com.nhnacademy.byeol23backend.bookset.bookcategory.repository.BookCategoryRepository;
import com.nhnacademy.byeol23backend.bookset.bookimage.repository.BookImageRepository;
import com.nhnacademy.byeol23backend.bookset.category.dto.CategoryLeafResponse;
import com.nhnacademy.byeol23backend.bookset.category.dto.CategoryListResponse;
import com.nhnacademy.byeol23backend.bookset.category.dto.CategoryMainPageResponse;
import com.nhnacademy.byeol23backend.bookset.category.dto.CategoryTreeResponse;
import com.nhnacademy.byeol23backend.bookset.category.dto.SubCategoryIdListResponse;
import com.nhnacademy.byeol23backend.bookset.category.repository.CategoryRepository;
import com.nhnacademy.byeol23backend.bookset.category.service.CategoryQueryService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CategoryQueryServiceImpl implements CategoryQueryService {
    private final CategoryRepository categoryRepository;
	private final BookCategoryRepository bookCategoryRepository;
	private final BookRepository bookRepository;
	private final BookImageRepository bookImageRepository;

	@Override
    public List<CategoryListResponse> getRootCategories() {
        return categoryRepository.findRootCategories();
    }

    @Override
    public List<CategoryListResponse> getSubCategories(Long parentId) {
        return categoryRepository.findChildrenCategories(parentId);
    }

	@Override
	public List<CategoryLeafResponse> getLeafCategories() {
		return categoryRepository.findLeafCategories();
	}

	@Override
	public List<CategoryMainPageResponse> getLeafCategoriesForMainPage() {
		List<CategoryLeafResponse> leafCategories = categoryRepository.findLeafCategories();


		return leafCategories.stream()
			.map(leaf -> new CategoryMainPageResponse(
				leaf.id(),
				leaf.categoryName(),
				leaf.pathId(),
				bookCategoryRepository.countBookCategoriesByCategoryId(leaf.id()),
				bookImageRepository.findBookImageByBookId(bookCategoryRepository.findRepBookIdByCategoryId(leaf.id()))
			))
			.toList(); // or .collect(Collectors.toList()) if you're on older Java
	}

    @Override
    @Transactional(readOnly = true)
    public List<CategoryTreeResponse> getCategoriesWithChildren2Depth() {
        return categoryRepository.findRootCategoryEntities().stream()
                .map(root -> CategoryTreeResponse.from(root, 2)).toList();
    }

    @Override
    public List<SubCategoryIdListResponse> getSubCategoryIdsByPathId(String pathId) {
        return categoryRepository.findSubCategoryIdsByPathId(pathId);
    }
}
