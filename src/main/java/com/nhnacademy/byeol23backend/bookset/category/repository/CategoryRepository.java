package com.nhnacademy.byeol23backend.bookset.category.repository;

import com.nhnacademy.byeol23backend.bookset.category.domain.Category;
import com.nhnacademy.byeol23backend.bookset.category.dto.CategoryListResponse;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface CategoryRepository extends JpaRepository<Category, Long> {
    @Query("""
        select new com.nhnacademy.byeol23backend.bookset.category.dto.CategoryListResponse(
                c.categoryId, c.categoryName, (select count(ch) > 0 from Category ch where ch.parent.categoryId = c.categoryId))
        from Category c where c.parent.categoryId is null order by c.categoryName
        """)
    List<CategoryListResponse> findRootCategories();

    @Query("""
        select new com.nhnacademy.byeol23backend.bookset.category.dto.CategoryListResponse(
                c.categoryId, c.categoryName, (select count(ch) > 0 from Category ch where ch.parent.categoryId = c.categoryId))
        from Category c where c.parent.categoryId = :parentId order by c.categoryName
        """)
    List<CategoryListResponse> findChildrenCategories(@Param("parentId") Long parentId);

    @Modifying(clearAutomatically = true)
    @Query(value = """
        update categories
        set path_name = replace(path_name, :oldPathName, :newPathName) 
        where path_id like concat(:oldPathId, '/%') or path_id = :oldPathId
        """, nativeQuery = true)
    int updateSubPathNames(
            @Param("oldPathId") String oldPathId,
            @Param("oldPathName") String oldPathName,
            @Param("newPathName") String newPathName
    );
}
