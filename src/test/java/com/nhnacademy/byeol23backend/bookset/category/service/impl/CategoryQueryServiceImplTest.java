package com.nhnacademy.byeol23backend.bookset.category.service.impl;

import com.nhnacademy.byeol23backend.bookset.category.dto.CategoryLeafResponse;
import com.nhnacademy.byeol23backend.bookset.category.dto.CategoryListResponse;
import com.nhnacademy.byeol23backend.bookset.category.repository.CategoryRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CategoryQueryServiceImplTest {
    @Mock
    private CategoryRepository categoryRepository;
    @InjectMocks
    private CategoryQueryServiceImpl categoryQueryService;

    @Test
    void getRootCategories() {
        CategoryListResponse r1 = new CategoryListResponse(1L, "국내도서", true);
        CategoryListResponse r2 = new CategoryListResponse(2L, "해외도서", false);
        when(categoryRepository.findRootCategories()).thenReturn(List.of(r1, r2));

        List<CategoryListResponse> rootCategories = categoryQueryService.getRootCategories();

        assertEquals(2, rootCategories.size());
        assertThat(rootCategories).containsExactly(r1, r2);

        verify(categoryRepository, times(1)).findRootCategories();
    }

    @Test
    void getSubCategories() {
        CategoryListResponse r1 = new CategoryListResponse(10L, "소설",   false);
        when(categoryRepository.findChildrenCategories(1L)).thenReturn(List.of(r1));

        List<CategoryListResponse> subCategories = categoryQueryService.getSubCategories(1L);

        assertEquals(1, subCategories.size());
        assertThat(subCategories).containsExactly(r1);

        verify(categoryRepository, times(1)).findChildrenCategories(1L);
    }

    @Test
    void getLeafCategories() {
        CategoryLeafResponse r1 = new CategoryLeafResponse(20L, "소설", "국내도서/소설");
        when(categoryRepository.findLeafCategories()).thenReturn(List.of(r1));

        List<CategoryLeafResponse> leafCategories = categoryQueryService.getLeafCategories();

        assertEquals(1, leafCategories.size());
        assertThat(leafCategories).containsExactly(r1);

        verify(categoryRepository, times(1)).findLeafCategories();
    }
}