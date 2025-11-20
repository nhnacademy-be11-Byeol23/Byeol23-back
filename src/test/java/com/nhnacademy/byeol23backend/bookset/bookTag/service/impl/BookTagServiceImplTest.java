package com.nhnacademy.byeol23backend.bookset.bookTag.service.impl;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.*;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import com.nhnacademy.byeol23backend.bookset.book.domain.Book;
import com.nhnacademy.byeol23backend.bookset.booktag.domain.BookTag;
import com.nhnacademy.byeol23backend.bookset.booktag.repository.BookTagRepository;
import com.nhnacademy.byeol23backend.bookset.booktag.service.impl.BookTagServiceImpl;
import com.nhnacademy.byeol23backend.bookset.tag.domain.Tag;
import com.nhnacademy.byeol23backend.bookset.tag.repository.TagRepository;

@ExtendWith(MockitoExtension.class)
class BookTagServiceImplTest {

	@Mock
	private BookTagRepository bookTagRepository;

	@Mock
	private TagRepository tagRepository;

	@InjectMocks
	private BookTagServiceImpl bookTagService;

	@Test
	@DisplayName("getTagsByBookId - 도서 ID로 태그 조회 성공")
	void getTagsByBookId_Success() {
		// given
		Long bookId = 1L;
		Tag tag1 = new Tag("노벨문학상");
		ReflectionTestUtils.setField(tag1, "tagId", 1L);
		Tag tag2 = new Tag("베스트셀러");
		ReflectionTestUtils.setField(tag2, "tagId", 2L);

		List<Tag> tags = List.of(tag1, tag2);
		given(bookTagRepository.findTagsByBookId(bookId)).willReturn(tags);

		// when
		List<Tag> result = bookTagService.getTagsByBookId(bookId);

		// then
		assertThat(result).isNotNull();
		assertThat(result).hasSize(2);
		assertThat(result.get(0).getTagName()).isEqualTo("노벨문학상");
		assertThat(result.get(1).getTagName()).isEqualTo("베스트셀러");
		verify(bookTagRepository, times(1)).findTagsByBookId(bookId);
	}

	@Test
	@DisplayName("getTagsByBookId - 태그가 없는 경우 빈 리스트 반환")
	void getTagsByBookId_WhenNoTags_ReturnsEmptyList() {
		// given
		Long bookId = 1L;
		given(bookTagRepository.findTagsByBookId(bookId)).willReturn(new ArrayList<>());

		// when
		List<Tag> result = bookTagService.getTagsByBookId(bookId);

		// then
		assertThat(result).isNotNull();
		assertThat(result).isEmpty();
		verify(bookTagRepository, times(1)).findTagsByBookId(bookId);
	}

	@Test
	@DisplayName("createBookTags - 태그 생성 성공")
	void createBookTags_Success() {
		// given
		Book book = new Book();
		ReflectionTestUtils.setField(book, "bookId", 1L);

		Tag tag1 = new Tag("노벨문학상");
		ReflectionTestUtils.setField(tag1, "tagId", 1L);
		Tag tag2 = new Tag("베스트셀러");
		ReflectionTestUtils.setField(tag2, "tagId", 2L);

		List<Long> tagIds = List.of(1L, 2L);
		List<Tag> tags = List.of(tag1, tag2);

		given(tagRepository.findAllById(tagIds)).willReturn(tags);
		given(bookTagRepository.saveAll(anyList())).willAnswer(invocation -> {
			List<BookTag> bookTags = invocation.getArgument(0);
			return bookTags;
		});

		// when
		bookTagService.createBookTags(book, tagIds);

		// then
		verify(tagRepository, times(1)).findAllById(tagIds);
		verify(bookTagRepository, times(1)).saveAll(anyList());
	}

	@Test
	@DisplayName("createBookTags - 빈 태그 ID 리스트인 경우 아무것도 하지 않음")
	void createBookTags_WhenEmptyList_DoesNothing() {
		// given
		Book book = new Book();
		ReflectionTestUtils.setField(book, "bookId", 1L);
		List<Long> emptyTagIds = List.of();

		// when
		bookTagService.createBookTags(book, emptyTagIds);

		// then
		verify(tagRepository, never()).findAllById(anyList());
		verify(bookTagRepository, never()).saveAll(anyList());
	}

	@Test
	@DisplayName("updateBookTags - 태그 추가 성공")
	void updateBookTags_AddTags_Success() {
		// given
		Book book = new Book();
		ReflectionTestUtils.setField(book, "bookId", 1L);

		Tag oldTag = new Tag("기존태그");
		ReflectionTestUtils.setField(oldTag, "tagId", 1L);

		Tag newTag = new Tag("새태그");
		ReflectionTestUtils.setField(newTag, "tagId", 2L);

		List<Tag> oldTags = List.of(oldTag);
		List<Long> newTagIds = List.of(1L, 2L);
		List<Tag> allTags = List.of(oldTag, newTag);

		given(bookTagRepository.findTagsByBookId(1L)).willReturn(oldTags);
		given(tagRepository.findAllById(newTagIds)).willReturn(allTags);
		given(bookTagRepository.saveAll(anyList())).willAnswer(invocation -> invocation.getArgument(0));

		// when
		bookTagService.updateBookTags(book, newTagIds);

		// then
		verify(bookTagRepository, times(1)).findTagsByBookId(1L);
		verify(tagRepository, times(1)).findAllById(newTagIds);
		verify(bookTagRepository, times(1)).saveAll(anyList());
		verify(bookTagRepository, never()).deleteByBookIdAndTagIds(anyLong(), anyList());
	}

	@Test
	@DisplayName("updateBookTags - 태그 삭제 성공")
	void updateBookTags_DeleteTags_Success() {
		// given
		Book book = new Book();
		ReflectionTestUtils.setField(book, "bookId", 1L);

		Tag tag1 = new Tag("태그1");
		ReflectionTestUtils.setField(tag1, "tagId", 1L);
		Tag tag2 = new Tag("태그2");
		ReflectionTestUtils.setField(tag2, "tagId", 2L);

		List<Tag> oldTags = List.of(tag1, tag2);
		List<Long> newTagIds = List.of(1L);

		given(bookTagRepository.findTagsByBookId(1L)).willReturn(oldTags);
		doNothing().when(bookTagRepository).deleteByBookIdAndTagIds(anyLong(), anyList());

		// when
		bookTagService.updateBookTags(book, newTagIds);

		// then
		verify(bookTagRepository, times(1)).findTagsByBookId(1L);
		verify(bookTagRepository, times(1)).deleteByBookIdAndTagIds(eq(1L), eq(List.of(2L)));
	}

	@Test
	@DisplayName("updateBookTags - 태그 추가 및 삭제 동시 수행 성공")
	void updateBookTags_AddAndDeleteTags_Success() {
		// given
		Book book = new Book();
		ReflectionTestUtils.setField(book, "bookId", 1L);

		Tag oldTag1 = new Tag("태그1");
		ReflectionTestUtils.setField(oldTag1, "tagId", 1L);
		Tag oldTag2 = new Tag("태그2");
		ReflectionTestUtils.setField(oldTag2, "tagId", 2L);

		Tag newTag = new Tag("태그3");
		ReflectionTestUtils.setField(newTag, "tagId", 3L);

		List<Tag> oldTags = List.of(oldTag1, oldTag2);
		List<Long> newTagIds = List.of(1L, 3L);
		List<Tag> tagsToAdd = List.of(oldTag1, newTag);

		given(bookTagRepository.findTagsByBookId(1L)).willReturn(oldTags);
		given(tagRepository.findAllById(newTagIds)).willReturn(tagsToAdd);
		given(bookTagRepository.saveAll(anyList())).willAnswer(invocation -> invocation.getArgument(0));
		doNothing().when(bookTagRepository).deleteByBookIdAndTagIds(anyLong(), anyList());

		// when
		bookTagService.updateBookTags(book, newTagIds);

		// then
		verify(bookTagRepository, times(1)).findTagsByBookId(1L);
		verify(tagRepository, times(1)).findAllById(newTagIds);
		verify(bookTagRepository, times(1)).saveAll(anyList());
		verify(bookTagRepository, times(1)).deleteByBookIdAndTagIds(eq(1L), eq(List.of(2L)));
	}

	@Test
	@DisplayName("updateBookTags - 변경사항이 없는 경우 아무것도 하지 않음")
	void updateBookTags_WhenNoChanges_DoesNothing() {
		// given
		Book book = new Book();
		ReflectionTestUtils.setField(book, "bookId", 1L);

		Tag tag1 = new Tag("태그1");
		ReflectionTestUtils.setField(tag1, "tagId", 1L);
		Tag tag2 = new Tag("태그2");
		ReflectionTestUtils.setField(tag2, "tagId", 2L);

		List<Tag> oldTags = List.of(tag1, tag2);
		List<Long> newTagIds = List.of(1L, 2L);

		given(bookTagRepository.findTagsByBookId(1L)).willReturn(oldTags);

		// when
		bookTagService.updateBookTags(book, newTagIds);

		// then
		verify(bookTagRepository, times(1)).findTagsByBookId(1L);
		verify(tagRepository, never()).findAllById(anyList());
		verify(bookTagRepository, never()).saveAll(anyList());
		verify(bookTagRepository, never()).deleteByBookIdAndTagIds(anyLong(), anyList());
	}
}

