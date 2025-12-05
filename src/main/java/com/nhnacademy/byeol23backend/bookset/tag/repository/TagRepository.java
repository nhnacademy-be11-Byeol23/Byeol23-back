package com.nhnacademy.byeol23backend.bookset.tag.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.nhnacademy.byeol23backend.bookset.tag.domain.Tag;

@Repository
public interface TagRepository extends JpaRepository<Tag, Long> {
	Tag getTagByTagId(Long tagId);

	void deleteTagByTagId(Long tagId);

	Optional<Tag> findTagByTagId(Long tagId);

 	Optional<Tag> findByTagId(Long tagId);

	 @Query("select count(t) from Tag t where t.tagName = :tagName")
	Long findTagByTagName(String tagName);
}
