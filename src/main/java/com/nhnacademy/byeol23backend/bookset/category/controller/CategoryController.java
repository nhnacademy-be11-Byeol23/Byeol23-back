package com.nhnacademy.byeol23backend.bookset.category.controller;

import com.nhnacademy.byeol23backend.bookset.category.dto.*;
import com.nhnacademy.byeol23backend.bookset.category.exception.CategoryDeleteReferencedByBookException;
import com.nhnacademy.byeol23backend.bookset.category.exception.CategoryNotFoundException;
import com.nhnacademy.byeol23backend.commons.exception.ErrorResponse;
import com.nhnacademy.byeol23backend.bookset.category.service.CategoryCacheService;
import com.nhnacademy.byeol23backend.bookset.category.service.CategoryCommandService;
import com.nhnacademy.byeol23backend.bookset.category.service.CategoryQueryService;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class CategoryController {
    private final CategoryQueryService categoryQueryService;
    private final CategoryCommandService categoryCommandService;
    private final CategoryCacheService categoryCacheService;

    @PostMapping("/categories")
    @ResponseStatus(HttpStatus.CREATED)
    public void createCategory(@RequestBody @Valid CategoryCreateRequest createRequest) {
        categoryCommandService.createCategory(createRequest);
    }

    @PutMapping("/categories/{id}")
    @ResponseStatus(HttpStatus.OK)
    public CategoryUpdateResponse updateCategory(@PathVariable("id") @NotNull @Min(1) Long id, @RequestBody @Valid CategoryUpdateRequest updateRequest) {
        return categoryCommandService.updateCategory(id, updateRequest);
    }

    @DeleteMapping("/categories/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteCategory(@PathVariable("id") @NotNull @Min(1) Long id) {
        categoryCommandService.deleteCategory(id);
    }

    @GetMapping("/categories/roots")
    @ResponseStatus(HttpStatus.OK)
    public List<CategoryListResponse> getRoots() {
        return categoryQueryService.getRootCategories();
    }

    @GetMapping("/categories/{parentId}/children")
    @ResponseStatus(HttpStatus.OK)
    public List<CategoryListResponse> getChildren(@PathVariable("parentId") @NotNull @Min(1) Long parentId) {
        return categoryQueryService.getSubCategories(parentId);
    }

    @GetMapping("/categories/leaf")
    public List<CategoryLeafResponse> getLeafs() {
        return categoryQueryService.getLeafCategories();
    }

    @GetMapping("/categories/tree")
    @ResponseStatus(HttpStatus.OK)
    public List<CategoryTreeResponse> getRootsWithChildren2Depth() {
        return categoryCacheService.getRootsWithChildren2Depth();
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleMethodArgumentNotValidException(MethodArgumentNotValidException e, HttpServletRequest request) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ErrorResponse(400, getErrorMessage(e), request.getRequestURI(), LocalDateTime.now()));
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ErrorResponse> handleConstraintViolationException(ConstraintViolationException e, HttpServletRequest request) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ErrorResponse(400, getErrorMessage(e), request.getRequestURI(), LocalDateTime.now()));
    }

    @ExceptionHandler(CategoryDeleteReferencedByBookException.class)
    public ResponseEntity<ErrorResponse> handleCategoryDeleteReferencedByBookException(CategoryDeleteReferencedByBookException e, HttpServletRequest request) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ErrorResponse(400, e.getMessage(), request.getRequestURI(), LocalDateTime.now()));
    }

    @ExceptionHandler(CategoryNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleCategoryNotFoundException(CategoryNotFoundException e, HttpServletRequest request) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ErrorResponse(404, e.getMessage(), request.getRequestURI(), LocalDateTime.now()));
    }

    private String getErrorMessage(MethodArgumentNotValidException e) {
        return e.getBindingResult().getFieldErrors().stream()
                .map(fieldError -> fieldError.getField() + ": " + fieldError.getDefaultMessage())
                .collect(Collectors.joining(",\n"));
    }

    private String getErrorMessage(ConstraintViolationException e) {
        return e.getConstraintViolations().stream()
                .map(constraintViolation -> constraintViolation.getPropertyPath().toString() + ": " + constraintViolation.getMessage())
                .collect(Collectors.joining(",\n"));
    }
}
