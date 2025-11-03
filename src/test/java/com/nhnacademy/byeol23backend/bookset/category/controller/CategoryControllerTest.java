package com.nhnacademy.byeol23backend.bookset.category.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nhnacademy.byeol23backend.bookset.book.interceptor.ViewerIdInterceptor;
import com.nhnacademy.byeol23backend.bookset.category.dto.*;
import com.nhnacademy.byeol23backend.bookset.category.exception.CategoryDeleteReferencedByBookException;
import com.nhnacademy.byeol23backend.bookset.category.exception.CategoryNotFoundException;
import com.nhnacademy.byeol23backend.bookset.category.service.impl.CategoryCommandServiceImpl;
import com.nhnacademy.byeol23backend.bookset.category.service.impl.CategoryQueryServiceImpl;
import com.nhnacademy.byeol23backend.config.WebConfig;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(value = CategoryController.class,
excludeFilters = @ComponentScan.Filter(type= FilterType.ASSIGNABLE_TYPE, classes = {ViewerIdInterceptor.class, WebConfig.class}))
class CategoryControllerTest {
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;
    @MockitoBean
    private CategoryCommandServiceImpl categoryCommandService;
    @MockitoBean
    private CategoryQueryServiceImpl categoryQueryService;


    @Test
    @DisplayName("카테고리 생성 성공")
    void createCategory_success() throws Exception {
        CategoryCreateRequest request = new CategoryCreateRequest("국내도서", null);

        mockMvc.perform(post("/api/categories")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated());

        Mockito.verify(categoryCommandService, Mockito.times(1))
                .createCategory(any(CategoryCreateRequest.class));
    }

    @Test
    @DisplayName("카테고리 수정 성공")
    void updateCategory_success() throws Exception {
        CategoryUpdateRequest updateRequest = new CategoryUpdateRequest("소설");
        CategoryUpdateResponse response = new CategoryUpdateResponse("소설");

        Mockito.when(categoryCommandService.updateCategory(eq(1L), any(CategoryUpdateRequest.class)))
                .thenReturn(response);

        mockMvc.perform(put("/api/categories/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("소설"));
    }

    @Test
    @DisplayName("카테고리 수정 실패 - 카테고리를 찾을 수 없음")
    void updateCategory_notFound() throws Exception {
        CategoryUpdateRequest updateRequest = new CategoryUpdateRequest("소설");
        Mockito.when(categoryCommandService.updateCategory(eq(1L), any(CategoryUpdateRequest.class)))
                .thenThrow(new CategoryNotFoundException("카테고리를 찾을 수 없습니다."));

        mockMvc.perform(put("/api/categories/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateRequest)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.message").value("카테고리를 찾을 수 없습니다."));
    }

    @Test
    @DisplayName("카테고리 삭제 성공")
    void deleteCategory_success() throws Exception {
        mockMvc.perform(delete("/api/categories/1"))
                .andExpect(status().isNoContent());

        Mockito.verify(categoryCommandService, Mockito.times(1)).deleteCategory(1L);
    }
    @Test
    @DisplayName("카테고리 삭제 실패 - 참조 하는 도서 존재")
    void deleteCategory_referencedByBook() throws Exception {
        Mockito.doThrow(new CategoryDeleteReferencedByBookException("카테고리를 참조하는 도서가 존재합니다."))
                .when(categoryCommandService).deleteCategory(1L);

        mockMvc.perform(delete("/api/categories/1"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("카테고리를 참조하는 도서가 존재합니다."));
    }

    @Test
    @DisplayName("루트 카테고리 목록 조회 성공")
    void getRootCategories_success() throws Exception {
        List<CategoryListResponse> mockResponse = List.of(
                new CategoryListResponse(1L, "국내도서", true),
                new CategoryListResponse(2L, "소설", false)
        );

        Mockito.when(categoryQueryService.getRootCategories()).thenReturn(mockResponse);

        mockMvc.perform(get("/api/categories/roots"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].name").value("국내도서"))
                .andExpect(jsonPath("$[0].hasChildren").value(true))
                .andExpect(jsonPath("$[1].name").value("소설"))
                .andExpect(jsonPath("$[1].hasChildren").value(false));
    }

    @Test
    @DisplayName("하위 카테고리 목록 조회 성공")
    void getChildren_success() throws Exception {
        List<CategoryListResponse> mockResponse = List.of(
                new CategoryListResponse(3L, "한국 소설", false),
                new CategoryListResponse(4L, "외국 소설", false)
        );

        Mockito.when(categoryQueryService.getSubCategories(1L)).thenReturn(mockResponse);

        mockMvc.perform(get("/api/categories/1/children"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(3))
                .andExpect(jsonPath("$[0].name").value("한국 소설"))
                .andExpect(jsonPath("$[0].hasChildren").value(false))
                .andExpect(jsonPath("$[1].id").value(4))
                .andExpect(jsonPath("$[1].name").value("외국 소설"));
    }

    @Test
    @DisplayName("리프 카테고리 목록 조회 성공")
    void getLeafCategories_success() throws Exception {
        List<CategoryLeafResponse> mockResponse = List.of(
                new CategoryLeafResponse(10L, "경제", "인문/경제"),
                new CategoryLeafResponse(11L, "IT", "기술/IT")
        );

        Mockito.when(categoryQueryService.getLeafCategories()).thenReturn(mockResponse);

        mockMvc.perform(get("/api/categories"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(10))
                .andExpect(jsonPath("$[0].categoryName").value("경제"))
                .andExpect(jsonPath("$[0].pathName").value("인문/경제"))
                .andExpect(jsonPath("$[1].id").value(11))
                .andExpect(jsonPath("$[1].pathName").value("기술/IT"))
                .andExpect(jsonPath("$[1].categoryName").value("IT"));
    }
}