package com.nhnacademy.byeol23backend.bookset.bookcategory.repository;

import com.nhnacademy.byeol23backend.bookset.bookcategory.domain.BookCategory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface BookCategoryRepository extends JpaRepository<BookCategory, Long> {

    @Query("select case when count(c) > 0 then true else false end from BookCategory bc join Category c on bc.category.categoryId = c.categoryId where c.pathId like concat(:pathId, '%')")
    boolean existsByCategoryPathIdLike(@Param("pathId") String pathId);
}
