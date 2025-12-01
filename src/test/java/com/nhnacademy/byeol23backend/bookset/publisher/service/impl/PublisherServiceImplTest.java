package com.nhnacademy.byeol23backend.bookset.publisher.service.impl;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.*;

import java.util.List;
import java.util.Optional;

import com.nhnacademy.byeol23backend.bookset.book.repository.BookRepository;
import com.nhnacademy.byeol23backend.bookset.publisher.domain.Publisher;
import com.nhnacademy.byeol23backend.bookset.publisher.domain.dto.AllPublishersInfoResponse;
import com.nhnacademy.byeol23backend.bookset.publisher.domain.dto.PublisherCreateRequest;
import com.nhnacademy.byeol23backend.bookset.publisher.domain.dto.PublisherCreateResponse;
import com.nhnacademy.byeol23backend.bookset.publisher.domain.dto.PublisherInfoResponse;
import com.nhnacademy.byeol23backend.bookset.publisher.domain.dto.PublisherUpdateRequest;
import com.nhnacademy.byeol23backend.bookset.publisher.domain.dto.PublisherUpdateResponse;
import com.nhnacademy.byeol23backend.bookset.publisher.exception.PublisherNotFoundException;
import com.nhnacademy.byeol23backend.bookset.publisher.exception.RelatedBookExistsException;
import com.nhnacademy.byeol23backend.bookset.publisher.repository.PublisherRepository;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.util.ReflectionTestUtils;

@Disabled
@ExtendWith(MockitoExtension.class)
class PublisherServiceImplTest {

	@Mock
	private PublisherRepository publisherRepository;

	@Mock
	private BookRepository bookRepository;

	@InjectMocks
	private PublisherServiceImpl publisherService;

	@Test
	@DisplayName("출판사 생성 성공")
	void createPublisher_Success() {
		// given
		PublisherCreateRequest request = new PublisherCreateRequest("민음사");
		Publisher saved = new Publisher("민음사");
		ReflectionTestUtils.setField(saved, "publisherId", 1L);

		given(publisherRepository.save(any(Publisher.class))).willAnswer(invocation -> {
			Publisher p = invocation.getArgument(0);
			ReflectionTestUtils.setField(p, "publisherId", 1L);
			return p;
		});

		// when
		PublisherCreateResponse result = publisherService.createPublisher(request);

		// then
		assertThat(result).isNotNull();
		assertThat(result.publisher().getPublisherId()).isEqualTo(1L);
		assertThat(result.publisher().getPublisherName()).isEqualTo("민음사");

		verify(publisherRepository, times(1)).save(any(Publisher.class));
	}

	@Test
	@DisplayName("출판사 단건 조회 성공 - getPublisherByPublisherId")
	void getPublisherByPublisherId_Success() {
		// given
		Long publisherId = 1L;
		Publisher publisher = new Publisher("민음사");
		ReflectionTestUtils.setField(publisher, "publisherId", publisherId);

		given(publisherRepository.getPublisherByPublisherId(publisherId)).willReturn(publisher);

		// when
		PublisherInfoResponse result = publisherService.getPublisherByPublisherId(publisherId);

		// then
		assertThat(result).isNotNull();
		assertThat(result.publisher().getPublisherId()).isEqualTo(publisherId);
		assertThat(result.publisher().getPublisherName()).isEqualTo("민음사");

		verify(publisherRepository, times(1)).getPublisherByPublisherId(publisherId);
	}

	@Test
	@DisplayName("출판사 삭제 실패 - 관련 도서 존재 (RelatedBookExistsException)")
	void deletePublisherByPublisherId_Fail_RelatedBooksExist() {
		// given
		Long publisherId = 1L;

		given(bookRepository.countBooksByPublisherId(publisherId)).willReturn(3L);

		// when & then
		assertThatThrownBy(() -> publisherService.deletePublisherByPublisherId(publisherId))
			.isInstanceOf(RelatedBookExistsException.class)
			.hasMessageContaining("출판사에서 출판한 책이 아직 존재합니다.");

		verify(bookRepository, times(1)).countBooksByPublisherId(publisherId);
		verify(publisherRepository, never()).deletePublisherByPublisherId(anyLong());
	}

	@Test
	@DisplayName("출판사 삭제 실패 - 존재하지 않는 출판사 (PublisherNotFoundException)")
	void deletePublisherByPublisherId_Fail_NotFound() {
		// given
		Long publisherId = 999L;

		given(bookRepository.countBooksByPublisherId(publisherId)).willReturn(0L);
		given(publisherRepository.findByPublisherId(publisherId)).willReturn(Optional.empty());

		// when & then
		assertThatThrownBy(() -> publisherService.deletePublisherByPublisherId(publisherId))
			.isInstanceOf(PublisherNotFoundException.class)
			.hasMessageContaining("해당 아이디 태그를 찾을 수 없습니다");

		verify(bookRepository, times(1)).countBooksByPublisherId(publisherId);
		verify(publisherRepository, times(1)).findByPublisherId(publisherId);
		verify(publisherRepository, never()).deletePublisherByPublisherId(anyLong());
	}

	@Test
	@DisplayName("출판사 삭제 성공")
	void deletePublisherByPublisherId_Success() {
		// given
		Long publisherId = 1L;
		Publisher publisher = new Publisher("민음사");
		ReflectionTestUtils.setField(publisher, "publisherId", publisherId);

		given(bookRepository.countBooksByPublisherId(publisherId)).willReturn(0L);
		given(publisherRepository.findByPublisherId(publisherId)).willReturn(Optional.of(publisher));
		willDoNothing().given(publisherRepository).deletePublisherByPublisherId(publisherId);

		// when
		publisherService.deletePublisherByPublisherId(publisherId);

		// then
		verify(bookRepository, times(1)).countBooksByPublisherId(publisherId);
		verify(publisherRepository, times(1)).findByPublisherId(publisherId);
		verify(publisherRepository, times(1)).deletePublisherByPublisherId(publisherId);
	}

	@Test
	@DisplayName("출판사 수정 성공")
	void updatePublisherByPublisherId_Success() {
		// given
		Long publisherId = 1L;
		PublisherUpdateRequest request = new PublisherUpdateRequest("문학동네");

		Publisher existing = new Publisher("민음사");
		ReflectionTestUtils.setField(existing, "publisherId", publisherId);

		given(publisherRepository.findByPublisherId(publisherId)).willReturn(Optional.of(existing));
		given(publisherRepository.save(any(Publisher.class))).willAnswer(invocation -> invocation.getArgument(0));

		// when
		PublisherUpdateResponse result = publisherService.updatePublisherByPublisherId(publisherId, request);

		// then
		assertThat(result).isNotNull();
		assertThat(result.publisher().getPublisherId()).isEqualTo(publisherId);
		assertThat(result.publisher().getPublisherName()).isEqualTo("문학동네");

		verify(publisherRepository, times(1)).findByPublisherId(publisherId);
		verify(publisherRepository, times(1)).save(existing);
	}

	@Test
	@DisplayName("출판사 수정 실패 - 존재하지 않는 출판사")
	void updatePublisherByPublisherId_Fail_NotFound() {
		// given
		Long publisherId = 999L;
		PublisherUpdateRequest request = new PublisherUpdateRequest("문학동네");

		given(publisherRepository.findByPublisherId(publisherId)).willReturn(Optional.empty());

		// when & then
		assertThatThrownBy(() -> publisherService.updatePublisherByPublisherId(publisherId, request))
			.isInstanceOf(PublisherNotFoundException.class)
			.hasMessageContaining("해당 아이디 태그를 찾을 수 없습니다");

		verify(publisherRepository, times(1)).findByPublisherId(publisherId);
		verify(publisherRepository, never()).save(any(Publisher.class));
	}

	@Test
	@DisplayName("전체 출판사 페이징 조회 성공")
	void getAllPublishers_Success() {
		// given
		Pageable pageable = PageRequest.of(0, 10);

		Publisher publisher1 = new Publisher("민음사");
		ReflectionTestUtils.setField(publisher1, "publisherId", 1L);

		Publisher publisher2 = new Publisher("문학동네");
		ReflectionTestUtils.setField(publisher2, "publisherId", 2L);

		Page<Publisher> publisherPage = new PageImpl<>(List.of(publisher1, publisher2), pageable, 2);

		given(publisherRepository.findAll(pageable)).willReturn(publisherPage);

		// when
		Page<AllPublishersInfoResponse> result = publisherService.getAllPublishers(pageable);

		// then
		assertThat(result).isNotNull();
		assertThat(result.getContent()).hasSize(2);
		assertThat(result.getContent().get(0).publisherName()).isEqualTo("민음사");
		assertThat(result.getContent().get(1).publisherName()).isEqualTo("문학동네");

		verify(publisherRepository, times(1)).findAll(pageable);
	}

	@Test
	@DisplayName("이름으로 출판사 찾기 - 존재하는 경우 Optional 반환")
	void findPublisherByName_WhenExists() {
		// given
		Publisher publisher = new Publisher("민음사");
		ReflectionTestUtils.setField(publisher, "publisherId", 1L);

		given(publisherRepository.findByPublisherName("민음사")).willReturn(Optional.of(publisher));

		// when
		Optional<AllPublishersInfoResponse> result = publisherService.findPublisherByName("민음사");

		// then
		assertThat(result).isPresent();
		assertThat(result.get().publisherName()).isEqualTo("민음사");

		verify(publisherRepository, times(1)).findByPublisherName("민음사");
	}

	@Test
	@DisplayName("이름으로 출판사 찾기 - 존재하지 않는 경우 빈 Optional")
	void findPublisherByName_WhenNotExists() {
		// given
		given(publisherRepository.findByPublisherName("없는출판사")).willReturn(Optional.empty());

		// when
		Optional<AllPublishersInfoResponse> result = publisherService.findPublisherByName("없는출판사");

		// then
		assertThat(result).isEmpty();
		verify(publisherRepository, times(1)).findByPublisherName("없는출판사");
	}

	@Test
	@DisplayName("findOrCreatePublisher - 이미 존재하는 출판사 반환")
	void findOrCreatePublisher_WhenExists_ReturnsExisting() {
		// given
		Publisher publisher = new Publisher("민음사");
		ReflectionTestUtils.setField(publisher, "publisherId", 1L);

		given(publisherRepository.findByPublisherName("민음사")).willReturn(Optional.of(publisher));

		// when
		AllPublishersInfoResponse result = publisherService.findOrCreatePublisher("민음사");

		// then
		assertThat(result).isNotNull();
		assertThat(result.publisherId()).isEqualTo(1L);
		assertThat(result.publisherName()).isEqualTo("민음사");

		verify(publisherRepository, times(1)).findByPublisherName("민음사");
		verify(publisherRepository, never()).save(any(Publisher.class));
	}

	@Test
	@DisplayName("findOrCreatePublisher - 없으면 새로 생성해서 반환")
	void findOrCreatePublisher_WhenNotExists_CreatesNew() {
		// given
		String name = "새출판사";

		given(publisherRepository.findByPublisherName(name)).willReturn(Optional.empty());
		given(publisherRepository.save(any(Publisher.class))).willAnswer(invocation -> {
			Publisher p = invocation.getArgument(0);
			ReflectionTestUtils.setField(p, "publisherId", 10L);
			return p;
		});

		// when
		AllPublishersInfoResponse result = publisherService.findOrCreatePublisher(name);

		// then
		assertThat(result).isNotNull();
		assertThat(result.publisherId()).isEqualTo(10L);
		assertThat(result.publisherName()).isEqualTo("새출판사");

		verify(publisherRepository, times(1)).findByPublisherName(name);
		verify(publisherRepository, times(1)).save(any(Publisher.class));
	}
}
