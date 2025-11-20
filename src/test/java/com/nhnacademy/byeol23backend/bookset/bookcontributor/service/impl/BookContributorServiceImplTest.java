package com.nhnacademy.byeol23backend.bookset.bookcontributor.service.impl;

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
import com.nhnacademy.byeol23backend.bookset.bookcontributor.domain.BookContributor;
import com.nhnacademy.byeol23backend.bookset.bookcontributor.repository.BookContributorRepository;
import com.nhnacademy.byeol23backend.bookset.contributor.domain.Contributor;
import com.nhnacademy.byeol23backend.bookset.contributor.domain.ContributorRole;
import com.nhnacademy.byeol23backend.bookset.contributor.repository.ContributorRepository;

@ExtendWith(MockitoExtension.class)
class BookContributorServiceImplTest {

	@Mock
	private BookContributorRepository bookContributorRepository;

	@Mock
	private ContributorRepository contributorRepository;

	@InjectMocks
	private BookContributorServiceImpl bookContributorService;

	@Test
	@DisplayName("getContributorsByBookId - 도서 ID로 기여자 조회 성공")
	void getContributorsByBookId_Success() {
		// given
		Long bookId = 1L;
		Contributor contributor1 = new Contributor(1L, "홍길동", ContributorRole.AUTHOR);
		Contributor contributor2 = new Contributor(2L, "김철수", ContributorRole.TRANSLATOR);

		List<Contributor> contributors = List.of(contributor1, contributor2);
		given(bookContributorRepository.findContributorByBookId(bookId)).willReturn(contributors);

		// when
		List<Contributor> result = bookContributorService.getContributorsByBookId(bookId);

		// then
		assertThat(result).isNotNull();
		assertThat(result).hasSize(2);
		assertThat(result.get(0).getContributorName()).isEqualTo("홍길동");
		assertThat(result.get(1).getContributorName()).isEqualTo("김철수");
		verify(bookContributorRepository, times(1)).findContributorByBookId(bookId);
	}

	@Test
	@DisplayName("getContributorsByBookId - 기여자가 없는 경우 빈 리스트 반환")
	void getContributorsByBookId_WhenNoContributors_ReturnsEmptyList() {
		// given
		Long bookId = 1L;
		given(bookContributorRepository.findContributorByBookId(bookId)).willReturn(new ArrayList<>());

		// when
		List<Contributor> result = bookContributorService.getContributorsByBookId(bookId);

		// then
		assertThat(result).isNotNull();
		assertThat(result).isEmpty();
		verify(bookContributorRepository, times(1)).findContributorByBookId(bookId);
	}

	@Test
	@DisplayName("createBookContributors - 기여자 생성 성공")
	void createBookContributors_Success() {
		// given
		Book book = new Book();
		ReflectionTestUtils.setField(book, "bookId", 1L);

		Contributor contributor1 = new Contributor(1L, "홍길동", ContributorRole.AUTHOR);
		Contributor contributor2 = new Contributor(2L, "김철수", ContributorRole.TRANSLATOR);

		List<Long> contributorIds = List.of(1L, 2L);
		List<Contributor> contributors = List.of(contributor1, contributor2);

		given(contributorRepository.findAllById(contributorIds)).willReturn(contributors);
		given(bookContributorRepository.saveAll(anyList())).willAnswer(invocation -> {
			List<BookContributor> bookContributors = invocation.getArgument(0);
			return bookContributors;
		});

		// when
		bookContributorService.createBookContributors(book, contributorIds);

		// then
		verify(contributorRepository, times(1)).findAllById(contributorIds);
		verify(bookContributorRepository, times(1)).saveAll(anyList());
	}

	@Test
	@DisplayName("createBookContributors - 빈 기여자 ID 리스트인 경우 아무것도 하지 않음")
	void createBookContributors_WhenEmptyList_DoesNothing() {
		// given
		Book book = new Book();
		ReflectionTestUtils.setField(book, "bookId", 1L);
		List<Long> emptyContributorIds = List.of();

		// when
		bookContributorService.createBookContributors(book, emptyContributorIds);

		// then
		verify(contributorRepository, never()).findAllById(anyList());
		verify(bookContributorRepository, never()).saveAll(anyList());
	}

	@Test
	@DisplayName("updateBookContributors - 기여자 추가 성공")
	void updateBookContributors_AddContributors_Success() {
		// given
		Book book = new Book();
		ReflectionTestUtils.setField(book, "bookId", 1L);

		Contributor oldContributor = new Contributor(1L, "홍길동", ContributorRole.AUTHOR);
		Contributor newContributor = new Contributor(2L, "김철수", ContributorRole.TRANSLATOR);

		List<Contributor> oldContributors = List.of(oldContributor);
		List<Long> newContributorIds = List.of(1L, 2L);
		List<Contributor> allContributors = List.of(oldContributor, newContributor);

		given(bookContributorRepository.findContributorByBookId(1L)).willReturn(oldContributors);
		given(contributorRepository.findAllById(newContributorIds)).willReturn(allContributors);
		given(bookContributorRepository.saveAll(anyList())).willAnswer(invocation -> invocation.getArgument(0));

		// when
		bookContributorService.updateBookContributors(book, newContributorIds);

		// then
		verify(bookContributorRepository, times(1)).findContributorByBookId(1L);
		verify(contributorRepository, times(1)).findAllById(newContributorIds);
		verify(bookContributorRepository, times(1)).saveAll(anyList());
		verify(bookContributorRepository, never()).deleteByBookIdAndContributorIds(anyLong(), anyList());
	}

	@Test
	@DisplayName("updateBookContributors - 기여자 삭제 성공")
	void updateBookContributors_DeleteContributors_Success() {
		// given
		Book book = new Book();
		ReflectionTestUtils.setField(book, "bookId", 1L);

		Contributor contributor1 = new Contributor(1L, "홍길동", ContributorRole.AUTHOR);
		Contributor contributor2 = new Contributor(2L, "김철수", ContributorRole.TRANSLATOR);

		List<Contributor> oldContributors = List.of(contributor1, contributor2);
		List<Long> newContributorIds = List.of(1L);

		given(bookContributorRepository.findContributorByBookId(1L)).willReturn(oldContributors);
		doNothing().when(bookContributorRepository).deleteByBookIdAndContributorIds(anyLong(), anyList());

		// when
		bookContributorService.updateBookContributors(book, newContributorIds);

		// then
		verify(bookContributorRepository, times(1)).findContributorByBookId(1L);
		verify(bookContributorRepository, times(1)).deleteByBookIdAndContributorIds(eq(1L), eq(List.of(2L)));
	}

	@Test
	@DisplayName("updateBookContributors - 기여자 추가 및 삭제 동시 수행 성공")
	void updateBookContributors_AddAndDeleteContributors_Success() {
		// given
		Book book = new Book();
		ReflectionTestUtils.setField(book, "bookId", 1L);

		Contributor oldContributor1 = new Contributor(1L, "홍길동", ContributorRole.AUTHOR);
		Contributor oldContributor2 = new Contributor(2L, "이영희", ContributorRole.AUTHOR);
		Contributor newContributor = new Contributor(3L, "김철수", ContributorRole.TRANSLATOR);

		List<Contributor> oldContributors = List.of(oldContributor1, oldContributor2);
		List<Long> newContributorIds = List.of(1L, 3L);
		List<Contributor> contributorsToAdd = List.of(oldContributor1, newContributor);

		given(bookContributorRepository.findContributorByBookId(1L)).willReturn(oldContributors);
		given(contributorRepository.findAllById(newContributorIds)).willReturn(contributorsToAdd);
		given(bookContributorRepository.saveAll(anyList())).willAnswer(invocation -> invocation.getArgument(0));
		doNothing().when(bookContributorRepository).deleteByBookIdAndContributorIds(anyLong(), anyList());

		// when
		bookContributorService.updateBookContributors(book, newContributorIds);

		// then
		verify(bookContributorRepository, times(1)).findContributorByBookId(1L);
		verify(contributorRepository, times(1)).findAllById(newContributorIds);
		verify(bookContributorRepository, times(1)).saveAll(anyList());
		verify(bookContributorRepository, times(1)).deleteByBookIdAndContributorIds(eq(1L), eq(List.of(2L)));
	}

	@Test
	@DisplayName("updateBookContributors - 변경사항이 없는 경우 아무것도 하지 않음")
	void updateBookContributors_WhenNoChanges_DoesNothing() {
		// given
		Book book = new Book();
		ReflectionTestUtils.setField(book, "bookId", 1L);

		Contributor contributor1 = new Contributor(1L, "홍길동", ContributorRole.AUTHOR);
		Contributor contributor2 = new Contributor(2L, "김철수", ContributorRole.TRANSLATOR);

		List<Contributor> oldContributors = List.of(contributor1, contributor2);
		List<Long> newContributorIds = List.of(1L, 2L);

		given(bookContributorRepository.findContributorByBookId(1L)).willReturn(oldContributors);

		// when
		bookContributorService.updateBookContributors(book, newContributorIds);

		// then
		verify(bookContributorRepository, times(1)).findContributorByBookId(1L);
		verify(contributorRepository, never()).findAllById(anyList());
		verify(bookContributorRepository, never()).saveAll(anyList());
		verify(bookContributorRepository, never()).deleteByBookIdAndContributorIds(anyLong(), anyList());
	}
}

