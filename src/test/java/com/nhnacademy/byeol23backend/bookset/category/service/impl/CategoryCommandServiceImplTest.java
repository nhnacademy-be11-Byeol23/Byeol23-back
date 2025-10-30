package com.nhnacademy.byeol23backend.bookset.category.service.impl;

import com.nhnacademy.byeol23backend.bookset.bookcategory.repository.BookCategoryRepository;
import com.nhnacademy.byeol23backend.bookset.category.domain.Category;
import com.nhnacademy.byeol23backend.bookset.category.dto.CategoryCreateRequest;
import com.nhnacademy.byeol23backend.bookset.category.dto.CategoryUpdateRequest;
import com.nhnacademy.byeol23backend.bookset.category.exception.CategoryDeleteReferencedByBookException;
import com.nhnacademy.byeol23backend.bookset.category.exception.CategoryNotFoundException;
import com.nhnacademy.byeol23backend.bookset.category.repository.CategoryRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Optional;

@ExtendWith(MockitoExtension.class)
class CategoryCommandServiceImplTest {
    @Mock
    private CategoryRepository categoryRepository;
    @Mock
    private BookCategoryRepository bookCategoryRepository;
    @InjectMocks
    private CategoryCommandServiceImpl categoryCommandService;

    @Test
    void createCategory_root_success() {
        CategoryCreateRequest categoryCreateRequest = new CategoryCreateRequest("root", null);
        Category savedCategory = new Category("root", null);
        ReflectionTestUtils.setField(savedCategory, "categoryId", 1L);
        Mockito.when(categoryRepository.save(Mockito.any(Category.class))).thenReturn(savedCategory);

        categoryCommandService.createCategory(categoryCreateRequest);

        Assertions.assertAll(
                () -> Assertions.assertEquals("1", savedCategory.getPathId()),
                () -> Assertions.assertEquals("root", savedCategory.getPathName())
        );
    }

    @Test
    void createCategory_child_success() {
        CategoryCreateRequest categoryCreateRequest = new CategoryCreateRequest("child", 1L);
        Category parent = new Category("parent", null);
        ReflectionTestUtils.setField(parent, "pathId", "1");
        ReflectionTestUtils.setField(parent, "pathName", "parent");

        Mockito.when(categoryRepository.findById(1L)).thenReturn(Optional.of(parent));
        Mockito.when(categoryRepository.save(Mockito.any(Category.class))).thenAnswer(invocation -> {
            Category c = invocation.getArgument(0);
            ReflectionTestUtils.setField(c, "categoryId", 2L);
            return c;
        });

        ArgumentCaptor<Category> captor = ArgumentCaptor.forClass(Category.class);

        categoryCommandService.createCategory(categoryCreateRequest);

        Mockito.verify(categoryRepository).save(captor.capture());
        Category savedCategory = captor.getValue();

        Assertions.assertAll(
                () -> Assertions.assertEquals("1/2", savedCategory.getPathId()),
                () -> Assertions.assertEquals("parent/child", savedCategory.getPathName())
        );
    }

    @Test
    void createCategory_parent_not_found() {
        CategoryCreateRequest categoryCreateRequest = new CategoryCreateRequest("child", 1L);
        Mockito.when(categoryRepository.findById(Mockito.anyLong())).thenReturn(Optional.empty());

        Assertions.assertThrows(CategoryNotFoundException.class, () -> categoryCommandService.createCategory(categoryCreateRequest));

        Mockito.verify(categoryRepository, Mockito.times(1)).findById(Mockito.anyLong());
    }

    @Test
    void updateCategory_root_success() {
        Category category = new Category("category", null);
        CategoryUpdateRequest categoryUpdateRequest = new CategoryUpdateRequest("update");

        Mockito.when(categoryRepository.findById(1L)).thenReturn(Optional.of(category));

        categoryCommandService.updateCategory(1L, categoryUpdateRequest);

        Assertions.assertAll(
                () -> Assertions.assertEquals("update", category.getCategoryName()),
                () -> Assertions.assertEquals("update", category.getPathName())
        );

        Mockito.verify(categoryRepository, Mockito.times(1)).findById(1L);
    }

    @Test
    void updateCategory_child_success() {
        Category parent = new Category("parent", null);
        ReflectionTestUtils.setField(parent, "pathId", "1");
        ReflectionTestUtils.setField(parent, "pathName", "parent");
        Category child = new Category("child", parent);
        ReflectionTestUtils.setField(child, "categoryId", 2L);
        ReflectionTestUtils.setField(child, "pathId", "1/2");
        ReflectionTestUtils.setField(child, "pathName", "parent/child");
        CategoryUpdateRequest categoryUpdateRequest = new CategoryUpdateRequest("childUpdate");

        Mockito.when(categoryRepository.findById(2L)).thenReturn(Optional.of(child));

        categoryCommandService.updateCategory(2L, categoryUpdateRequest);

        Assertions.assertAll(
                () -> Assertions.assertEquals("childUpdate", child.getCategoryName()),
                () -> Assertions.assertEquals("1/2", child.getPathId()),
                () -> Assertions.assertEquals("parent/childUpdate", child.getPathName())
        );

        Mockito.verify(categoryRepository, Mockito.times(1)).findById(2L);
    }

    @Test
    void updateCategory_not_found() {
        CategoryUpdateRequest categoryUpdateRequest = new CategoryUpdateRequest("update");
        Mockito.when(categoryRepository.findById(1L)).thenReturn(Optional.empty());

        Assertions.assertThrows(CategoryNotFoundException.class, () -> categoryCommandService.updateCategory(1L, categoryUpdateRequest));

        Mockito.verify(categoryRepository, Mockito.times(1)).findById(1L);
    }

    @Test
    void deleteCategory_success() {
        Category category = new Category("category", null);
        ReflectionTestUtils.setField(category, "categoryId", 1L);
        ReflectionTestUtils.setField(category, "pathId", "1");

        Mockito.when(categoryRepository.findById(1L)).thenReturn(Optional.of(category));
        Mockito.when(bookCategoryRepository.existsByCategoryPathIdLike("1")).thenReturn(false);

        categoryCommandService.deleteCategory(1L);

        Mockito.verify(categoryRepository, Mockito.times(1)).findById(1L);
        Mockito.verify(bookCategoryRepository, Mockito.times(1)).existsByCategoryPathIdLike("1");
        Mockito.verify(categoryRepository, Mockito.times(1)).deleteById(1L);
    }

    @Test
    void deleteCategory_not_found() {
        Mockito.when(categoryRepository.findById(1L)).thenReturn(Optional.empty());

        Assertions.assertThrows(CategoryNotFoundException.class, () -> categoryCommandService.deleteCategory(1L));

        Mockito.verify(categoryRepository, Mockito.times(1)).findById(1L);
        Mockito.verifyNoInteractions(bookCategoryRepository);
        Mockito.verify(categoryRepository, Mockito.never()).deleteById(1L);
    }

    @Test
    void deleteCategory_referenced_by_book() {
        Category category = new Category("category", null);
        ReflectionTestUtils.setField(category, "categoryId", 1L);
        ReflectionTestUtils.setField(category, "pathId", "1");

        Mockito.when(categoryRepository.findById(1L)).thenReturn(Optional.of(category));
        Mockito.when(bookCategoryRepository.existsByCategoryPathIdLike("1")).thenReturn(true);

        Assertions.assertThrows(CategoryDeleteReferencedByBookException.class, () -> categoryCommandService.deleteCategory(1L));

        Mockito.verify(categoryRepository, Mockito.times(1)).findById(1L);
        Mockito.verify(bookCategoryRepository, Mockito.times(1)).existsByCategoryPathIdLike("1");
        Mockito.verify(categoryRepository, Mockito.never()).deleteById(1L);
    }
}