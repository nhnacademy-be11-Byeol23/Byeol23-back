package com.nhnacademy.byeol23backend.bookset.category.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.nhnacademy.byeol23backend.bookset.category.domain.Category;
import com.nhnacademy.byeol23backend.bookset.category.dto.CategoryLeafResponse;
import com.nhnacademy.byeol23backend.bookset.category.dto.CategoryListResponse;
import com.nhnacademy.byeol23backend.bookset.category.dto.SubCategoryIdListResponse;

public interface CategoryRepository extends JpaRepository<Category, Long> {
    // 카테고리 테이블에서 최상위 루트 카테고리(parent_id 가 null인)를 조회하는 메서드
    @Query("""
        select new com.nhnacademy.byeol23backend.bookset.category.dto.CategoryListResponse(
                c.categoryId, c.categoryName, (select count(ch) > 0 from Category ch where ch.parent.categoryId = c.categoryId))
        from Category c where c.parent.categoryId is null order by c.categoryName
        """)
    List<CategoryListResponse> findRootCategories();

    // 카테고리 테이블에서 최상위 루트 카테고리 엔티티를 조회하는 메서드
    @Query("""
        select c from Category c where c.parent.categoryId is null order by c.categoryName
        """)
    List<Category> findRootCategoryEntities();

    // 카테고리 테이블에서 부모 카테고리의 직계 자손 카테고리를 조회하는 메서드
    @Query("""
        select new com.nhnacademy.byeol23backend.bookset.category.dto.CategoryListResponse(
                c.categoryId, c.categoryName, (select count(ch) > 0 from Category ch where ch.parent.categoryId = c.categoryId))
        from Category c where c.parent.categoryId = :parentId order by c.categoryName
        """)
    List<CategoryListResponse> findChildrenCategories(@Param("parentId") Long parentId);

    // 카테고리 이름을 수정했을 때 수정 되는 카테고리와 수정 되는 카테고리의 하위 카테고리의 계층 경로명을 변경하는 메서드
    // ex) 국내도서, 국내도서/소설, 국내도서/소설/해외소설 카테고리가 있을 때 국내도서의 하위 카테고리 소설을 국내도서/문학으로 변경했을 때
    // 국내도서, 국내도서/문학, 국내도서/문학/해외소설로 변경하는 메서드
    @Modifying(clearAutomatically = true)
    @Query(value = """
        update categories
        set path_name = replace(path_name, :oldPathName, :newPathName) 
        where path_id like concat(:pathId, '/%') or path_id = :pathId
        """, nativeQuery = true)
    void updateSubPathNames(
            @Param("pathId") String pathId,
            @Param("oldPathName") String oldPathName,
            @Param("newPathName") String newPathName
    );

    // leaf 카테고리 조회
    @Query("""
        select new com.nhnacademy.byeol23backend.bookset.category.dto.CategoryLeafResponse(c1.categoryId, c1.categoryName, c1.pathId)
                from Category c1 where not exists (select 1 from Category c2 where c1.categoryId = c2.parent.categoryId)
        """)
    List<CategoryLeafResponse> findLeafCategories();

    @Query("""
        select new com.nhnacademy.byeol23backend.bookset.category.dto.SubCategoryIdListResponse(c.categoryId) from Category c where c.pathId like concat(:pathId, '%') 
        """)
    List<SubCategoryIdListResponse> findSubCategoryIdsByPathId(@Param("pathId") String pathId);

    @Query(
            value = """
            WITH RECURSIVE CategoryAncestors AS (
                SELECT 
                    bc.category_id,
                    c.parent_id
                FROM 
                    book_category bc
                JOIN 
                    categories c ON bc.category_id = c.category_id
                WHERE 
                    bc.book_id IN (:bookIds)
                    
                UNION 
                
                SELECT 
                    c_parent.category_id,
                    c_parent.parent_id
                FROM 
                    categories c_parent
                JOIN 
                    CategoryAncestors ca ON c_parent.category_id = ca.parent_id
            )
            SELECT DISTINCT category_id
            FROM CategoryAncestors
            """,
            nativeQuery = true
    )
    List<Long> findAllAncestorsByBookIds(@Param("bookIds") List<Long> bookIds);

    @Query("SELECT COUNT(ccp) > 0 FROM CategoryCouponPolicy ccp " +
            "WHERE ccp.couponPolicy.couponPolicyId = :policyId " +
            "AND ccp.category.categoryId IN :itemCategoryIds")
    boolean isAnyCategoryIncludedInPolicy(@Param("itemCategoryIds") List<Long> itemCategoryIds, @Param("policyId") Long policyId);
}
