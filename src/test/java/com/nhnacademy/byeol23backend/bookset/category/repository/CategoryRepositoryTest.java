package com.nhnacademy.byeol23backend.bookset.category.repository;

import com.nhnacademy.byeol23backend.bookset.category.dto.CategoryLeafResponse;
import com.nhnacademy.byeol23backend.bookset.category.dto.CategoryListResponse;
import com.nhnacademy.byeol23backend.bookset.category.exception.CategoryNotFoundException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;
import com.nhnacademy.byeol23backend.bookset.category.domain.Category;

import java.util.List;

@ActiveProfiles("test")
@DataJpaTest
class CategoryRepositoryTest {

    @Autowired
    private CategoryRepository categoryRepository;

    @Test
    @DisplayName("부모가 없는 루트 카테고리 조회 테스트")
    void findRootCategories() {
        Category r1 = new Category("국내도서", null);
        Category r2 = new Category("해외도서", null);
        Category r1c1 = new Category("소설", r1);
        Category r1c2 = new Category("IT", r1);
        Category r1c1c1 = new Category("현대소설", r1c1);
        categoryRepository.saveAll(List.of(r1, r2, r1c1, r1c2, r1c1c1));

        List<CategoryListResponse> rootCategories = categoryRepository.findRootCategories();
        Assertions.assertEquals(2, rootCategories.size());

        CategoryListResponse root1 = rootCategories.stream()
                .filter(root -> root.name().equals("국내도서"))
                .findFirst().orElseThrow(() -> new CategoryNotFoundException("카테고리를 찾을 수 없습니다."));

        Assertions.assertTrue(root1.hasChildren());

        CategoryListResponse root2 = rootCategories.stream()
                .filter(root -> root.name().equals("해외도서"))
                .findFirst().orElseThrow(() -> new CategoryNotFoundException("카테고리를 찾을 수 없습니다."));

        Assertions.assertFalse(root2.hasChildren());
    }

    @Test
    @DisplayName("부모의 직계 자식 카테고리 조회 테스트")
    void findChildrenCategories() {
        Category r1 = new Category("국내도서", null);
        Category r1c1 = new Category("소설", r1);
        Category r1c2 = new Category("IT", r1);
        Category r1c1c1 = new Category("현대소설", r1c1);
        categoryRepository.saveAll(List.of(r1, r1c1, r1c2, r1c1c1));

        List<CategoryListResponse> childrenCategories = categoryRepository.findChildrenCategories(r1.getCategoryId());
        Assertions.assertEquals(2, childrenCategories.size());
    }

    @Test
    @DisplayName("카테고리명을 수정했을 때 수정 되는 카테고리와 수정 되는 카테고리의 하위 카테고리의 계층 경로명 수정 테스트")
    void updateCategoryPathName() {
        Category r1 = new Category("국내도서", null);
        categoryRepository.save(r1);
        r1.updatePath(String.valueOf(r1.getCategoryId()), r1.getCategoryName());
        Category r1c1 = new Category("IT", r1);
        categoryRepository.save(r1c1);
        r1c1.updatePath(r1.getPathId() + "/" + r1c1.getCategoryId(), r1.getPathName() + "/" + r1c1.getCategoryName());
        Category r1c1c1 = new Category("인공지능", r1c1);
        categoryRepository.save(r1c1c1);
        r1c1c1.updatePath(r1c1.getPathId() + "/" + r1c1c1.getCategoryId(), r1c1.getPathName() + "/" + r1c1c1.getCategoryName());

        categoryRepository.updateSubPathNames(r1c1.getPathId(), r1c1.getPathName(), r1c1.getParent().getPathName() + "/" + "테크");

        Category findr1c1 = categoryRepository.findById(r1c1.getCategoryId()).orElseThrow(() -> new CategoryNotFoundException("카테고리를 찾을 수 없습니다."));
        Category findr1c1c1 = categoryRepository.findById(r1c1c1.getCategoryId()).orElseThrow(() -> new CategoryNotFoundException("카테고리를 찾을 수 없습니다."));

        Assertions.assertEquals("국내도서/테크", findr1c1.getPathName());
        Assertions.assertEquals("국내도서/테크/인공지능", findr1c1c1.getPathName());
    }

    @Test
    @DisplayName("leaf 카테고리 조회 테스트")
    void findLeafCategories() {
        Category r1 = new Category("국내도서", null);
        Category r1c1 = new Category("소설", r1);
        Category r1c1c1 = new Category("현대소설", r1c1);
        Category r1c1c2 = new Category("고전소설", r1c1);
        categoryRepository.saveAll(List.of(r1, r1c1, r1c1c1, r1c1c2));

        List<CategoryLeafResponse> leafCategories = categoryRepository.findLeafCategories();

        Assertions.assertEquals(2, leafCategories.size());

    }
}