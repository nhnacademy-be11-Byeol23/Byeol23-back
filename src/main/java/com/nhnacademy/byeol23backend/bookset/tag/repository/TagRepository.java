package com.nhnacademy.byeol23backend.bookset.tag.repository;

import java.util.Optional;

import com.nhnacademy.byeol23backend.bookset.tag.domain.Tag;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TagRepository extends JpaRepository<Tag, Long> {
	Tag getTagByTagId(Long tagId);

	void deleteTagByTagId(Long tagId);

	Tag findTagByTagId(Long tagId);

 	Optional<Tag> findByTagId(Long tagId);
}
