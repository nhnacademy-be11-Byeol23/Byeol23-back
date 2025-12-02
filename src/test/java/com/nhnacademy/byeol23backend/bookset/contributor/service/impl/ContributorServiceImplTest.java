package com.nhnacademy.byeol23backend.bookset.contributor.service.impl;

import com.nhnacademy.byeol23backend.bookset.bookcontributor.repository.BookContributorRepository;
import com.nhnacademy.byeol23backend.bookset.contributor.domain.Contributor;
import com.nhnacademy.byeol23backend.bookset.contributor.domain.ContributorRole;
import com.nhnacademy.byeol23backend.bookset.contributor.domain.dto.*;
import com.nhnacademy.byeol23backend.bookset.contributor.exception.ContributorAlreadyExistsException;
import com.nhnacademy.byeol23backend.bookset.contributor.exception.ContributorNotFoundException;
import com.nhnacademy.byeol23backend.bookset.contributor.exception.RelatedBookExistsException;
import com.nhnacademy.byeol23backend.bookset.contributor.repository.ContributorRepository;
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

import java.util.List;
import java.util.Optional;

import static org.mockito.BDDMockito.given;
import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ContributorServiceImplTest {

    @Mock
    private ContributorRepository contributorRepository;

    @Mock
    private BookContributorRepository bookContributorRepository;

    @InjectMocks
    private ContributorServiceImpl contributorService;

    // ───────────────────────── getContributorByContributorId ─────────────────────────

    @Test
    @DisplayName("기여자 단건 조회 성공")
    void getContributorByContributorId_Success() {
        // given
        Long contributorId = 1L;
        Contributor contributor = new Contributor(contributorId, "저자명", ContributorRole.AUTHOR);
        given(contributorRepository.findById(contributorId))
                .willReturn(Optional.of(contributor));

        // when
        ContributorInfoResponse result = contributorService.getContributorByContributorId(contributorId);

        // then
        assertThat(result).isNotNull();
        assertThat(result.contributor().getContributorId()).isEqualTo(contributorId);
        assertThat(result.contributor().getContributorName()).isEqualTo("저자명");

        verify(contributorRepository, times(1)).findById(contributorId);
    }

    @Test
    @DisplayName("기여자 단건 조회 실패 - 존재하지 않는 ID")
    void getContributorByContributorId_NotFound() {
        // given
        Long contributorId = 999L;
        given(contributorRepository.findById(contributorId))
                .willReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> contributorService.getContributorByContributorId(contributorId))
                .isInstanceOf(ContributorNotFoundException.class)
                .hasMessageContaining("해당 기여자 없음");

        verify(contributorRepository, times(1)).findById(contributorId);
    }

    // ───────────────────────── createContributor ─────────────────────────

    @Test
    @DisplayName("기여자 생성 성공")
    void createContributor_Success() {
        // given
        ContributorCreateRequest request =
                new ContributorCreateRequest("새 저자", ContributorRole.AUTHOR);

        given(contributorRepository
                .findContributorByNameAndRole("새 저자", ContributorRole.AUTHOR))
                .willReturn(0L); // 중복 없음

        Contributor saved = new Contributor(1L, "새 저자", ContributorRole.AUTHOR);
        given(contributorRepository.save(any(Contributor.class)))
                .willReturn(saved);

        // when
        ContributorCreateResponse result = contributorService.createContributor(request);

        // then
        assertThat(result).isNotNull();
        assertThat(result.contributor().getContributorName()).isEqualTo("새 저자");
        assertThat(result.contributor().getContributorRole()).isEqualTo(ContributorRole.AUTHOR);

        verify(contributorRepository, times(1))
                .findContributorByNameAndRole("새 저자", ContributorRole.AUTHOR);
        verify(contributorRepository, times(1)).save(any(Contributor.class));
    }

    @Test
    @DisplayName("기여자 생성 실패 - request null")
    void createContributor_Fail_NullRequest() {
        // when & then
        assertThatThrownBy(() -> contributorService.createContributor(null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("request is null");

        verify(contributorRepository, never()).save(any());
    }

    @Test
    @DisplayName("기여자 생성 실패 - 이름이 null 또는 공백")
    void createContributor_Fail_BlankName() {
        // given
        ContributorCreateRequest request =
                new ContributorCreateRequest("  ", ContributorRole.AUTHOR);

        // when & then
        assertThatThrownBy(() -> contributorService.createContributor(request))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("name is required");

        verify(contributorRepository, never()).save(any());
    }

    @Test
    @DisplayName("기여자 생성 실패 - 역할 null")
    void createContributor_Fail_NullRole() {
        // given
        ContributorCreateRequest request =
                new ContributorCreateRequest("저자", null);

        // when & then
        assertThatThrownBy(() -> contributorService.createContributor(request))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("contributorRole is required");

        verify(contributorRepository, never()).save(any());
    }

    @Test
    @DisplayName("기여자 생성 실패 - 중복된 이름+역할")
    void createContributor_Fail_AlreadyExists() {
        // given
        ContributorCreateRequest request =
                new ContributorCreateRequest("중복저자", ContributorRole.AUTHOR);

        given(contributorRepository.findContributorByNameAndRole("중복저자", ContributorRole.AUTHOR))
                .willReturn(1L); // 이미 존재

        // when & then
        assertThatThrownBy(() -> contributorService.createContributor(request))
                .isInstanceOf(ContributorAlreadyExistsException.class)
                .hasMessageContaining("이미 존재합니다");

        verify(contributorRepository, times(1))
                .findContributorByNameAndRole("중복저자", ContributorRole.AUTHOR);
        verify(contributorRepository, never()).save(any());
    }

    // ───────────────────────── deleteContributorByContributorId ─────────────────────────

    @Test
    @DisplayName("기여자 삭제 성공 - 관련 도서 없음")
    void deleteContributorByContributorId_Success() {
        // given
        Long contributorId = 1L;
        given(bookContributorRepository.countBookContributorsByContributorId(contributorId))
                .willReturn(0L);

        // when
        contributorService.deleteContributorByContributorId(contributorId);

        // then
        verify(bookContributorRepository, times(1))
                .countBookContributorsByContributorId(contributorId);
        verify(contributorRepository, times(1))
                .deleteById(contributorId);
    }

    @Test
    @DisplayName("기여자 삭제 실패 - 관련 도서 존재")
    void deleteContributorByContributorId_Fail_RelatedBooksExist() {
        // given
        Long contributorId = 1L;
        given(bookContributorRepository.countBookContributorsByContributorId(contributorId))
                .willReturn(3L);

        // when & then
        assertThatThrownBy(() -> contributorService.deleteContributorByContributorId(contributorId))
                .isInstanceOf(RelatedBookExistsException.class)
                .hasMessageContaining("기여자가 기여한 책이 있습니다.");

        verify(bookContributorRepository, times(1))
                .countBookContributorsByContributorId(contributorId);
        verify(contributorRepository, never()).deleteById(anyLong());
    }

    // ───────────────────────── updateContributor ─────────────────────────

    @Test
    @DisplayName("기여자 수정 성공")
    void updateContributor_Success() {
        // given
        Long contributorId = 1L;
        ContributorUpdateRequest request =
                new ContributorUpdateRequest("수정된이름", "TRANSLATOR");

        // 중복 검사: 새 이름+역할로 다른 애가 없음을 가정 (0)
        given(contributorRepository.findContributorByNameAndRole(
                "수정된이름",
                ContributorRole.valueOf("TRANSLATOR")))
                .willReturn(0L);

        Contributor existing =
                new Contributor(contributorId, "기존이름", ContributorRole.AUTHOR);
        given(contributorRepository.findById(contributorId))
                .willReturn(Optional.of(existing));

        // when
        ContributorUpdateResponse result =
                contributorService.updateContributor(contributorId, request);

        // then
        assertThat(result).isNotNull();
        assertThat(result.contributor().getContributorId()).isEqualTo(contributorId);
        assertThat(result.contributor().getContributorName()).isEqualTo("수정된이름");
        // setContributorRole(String) 내부에서 enum 변환한다고 가정
        assertThat(result.contributor().getContributorRole())
                .isEqualTo(ContributorRole.TRANSLATOR);

        verify(contributorRepository, times(1))
                .findContributorByNameAndRole("수정된이름", ContributorRole.TRANSLATOR);
        verify(contributorRepository, times(1)).findById(contributorId);
    }

    @Test
    @DisplayName("기여자 수정 실패 - request null")
    void updateContributor_Fail_NullRequest() {
        // when & then
        assertThatThrownBy(() -> contributorService.updateContributor(1L, null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("request is null");

        verify(contributorRepository, never()).findById(anyLong());
    }

    @Test
    @DisplayName("기여자 수정 실패 - 이름이 null 또는 공백")
    void updateContributor_Fail_BlankName() {
        // given
        ContributorUpdateRequest request =
                new ContributorUpdateRequest("   ", "AUTHOR");

        // when & then
        assertThatThrownBy(() -> contributorService.updateContributor(1L, request))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("name is required");

        verify(contributorRepository, never()).findById(anyLong());
    }

    @Test
    @DisplayName("기여자 수정 실패 - 역할 null")
    void updateContributor_Fail_NullRole() {
        // given
        ContributorUpdateRequest request =
                new ContributorUpdateRequest("이름", null);

        // when & then
        assertThatThrownBy(() -> contributorService.updateContributor(1L, request))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("contributorRole is required");

        verify(contributorRepository, never()).findById(anyLong());
    }

    @Test
    @DisplayName("기여자 수정 실패 - 새 이름+역할이 이미 존재")
    void updateContributor_Fail_AlreadyExists() {
        // given
        Long contributorId = 1L;
        ContributorUpdateRequest request =
                new ContributorUpdateRequest("중복이름", "AUTHOR");

        given(contributorRepository.findContributorByNameAndRole(
                "중복이름",
                ContributorRole.valueOf("AUTHOR")))
                .willReturn(1L);

        // when & then
        assertThatThrownBy(() -> contributorService.updateContributor(contributorId, request))
                .isInstanceOf(ContributorAlreadyExistsException.class)
                .hasMessageContaining("이미 존재합니다");

        verify(contributorRepository, times(1))
                .findContributorByNameAndRole("중복이름", ContributorRole.AUTHOR);
        verify(contributorRepository, never()).findById(anyLong());
    }

    @Test
    @DisplayName("기여자 수정 실패 - 존재하지 않는 ID")
    void updateContributor_Fail_NotFound() {
        // given
        Long contributorId = 999L;
        ContributorUpdateRequest request =
                new ContributorUpdateRequest("이름", "AUTHOR");

        // 중복 없음
        given(contributorRepository.findContributorByNameAndRole(
                "이름",
                ContributorRole.AUTHOR))
                .willReturn(0L);
        given(contributorRepository.findById(contributorId))
                .willReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> contributorService.updateContributor(contributorId, request))
                .isInstanceOf(ContributorNotFoundException.class)
                .hasMessageContaining("해당 기여자 없음");

        verify(contributorRepository, times(1))
                .findContributorByNameAndRole("이름", ContributorRole.AUTHOR);
        verify(contributorRepository, times(1)).findById(contributorId);
    }

    // ───────────────────────── getAllContributors ─────────────────────────

    @Test
    @DisplayName("기여자 목록 조회 성공")
    void getAllContributors_Success() {
        // given
        int page = 0;
        int size = 10;
        Pageable pageable = PageRequest.of(page, size);

        Contributor c1 = new Contributor(1L, "저자1", ContributorRole.AUTHOR);
        Contributor c2 = new Contributor(2L, "저자2", ContributorRole.TRANSLATOR);

        Page<Contributor> contributorPage =
                new PageImpl<>(List.of(c1, c2), pageable, 2);

        given(contributorRepository.findAll(pageable))
                .willReturn(contributorPage);

        // when
        Page<AllContributorResponse> result =
                contributorService.getAllContributors(page, size);

        // then
        assertThat(result).isNotNull();
        assertThat(result.getContent()).hasSize(2);

        AllContributorResponse r1 = result.getContent().get(0);
        AllContributorResponse r2 = result.getContent().get(1);

        assertThat(r1.contributorId()).isEqualTo(1L);
        assertThat(r1.contributorName()).isEqualTo("저자1");
        assertThat(r1.contributorRole()).isEqualTo(ContributorRole.AUTHOR.getLabel());

        assertThat(r2.contributorId()).isEqualTo(2L);
        assertThat(r2.contributorName()).isEqualTo("저자2");
        assertThat(r2.contributorRole()).isEqualTo(ContributorRole.TRANSLATOR.getLabel());

        verify(contributorRepository, times(1)).findAll(pageable);
    }

    @Test
    @DisplayName("기여자 목록 조회 - 빈 페이지")
    void getAllContributors_Empty() {
        // given
        int page = 0;
        int size = 10;
        Pageable pageable = PageRequest.of(page, size);
        Page<Contributor> emptyPage = Page.empty(pageable);

        given(contributorRepository.findAll(pageable))
                .willReturn(emptyPage);

        // when
        Page<AllContributorResponse> result =
                contributorService.getAllContributors(page, size);

        // then
        assertThat(result).isNotNull();
        assertThat(result.getContent()).isEmpty();

        verify(contributorRepository, times(1)).findAll(pageable);
    }

    // ───────────────────────── findOrCreateContributor ─────────────────────────

    @Test
    @DisplayName("findOrCreateContributor - 이미 존재하는 경우 ID 반환")
    void findOrCreateContributor_Found() {
        // given
        Contributor existing =
                new Contributor(10L, "기존저자", ContributorRole.AUTHOR);

        given(contributorRepository.findByContributorNameAndContributorRole(
                "기존저자", ContributorRole.AUTHOR))
                .willReturn(Optional.of(existing));

        // when
        Long resultId =
                contributorService.findOrCreateContributor("기존저자", ContributorRole.AUTHOR);

        // then
        assertThat(resultId).isEqualTo(10L);
        verify(contributorRepository, times(1))
                .findByContributorNameAndContributorRole("기존저자", ContributorRole.AUTHOR);
        verify(contributorRepository, never()).save(any());
    }

    @Test
    @DisplayName("findOrCreateContributor - 없으면 새로 생성 후 ID 반환")
    void findOrCreateContributor_CreateNew() {
        // given
        given(contributorRepository.findByContributorNameAndContributorRole(
                "새저자", ContributorRole.TRANSLATOR))
                .willReturn(Optional.empty());

        Contributor saved =
                new Contributor(20L, "새저자", ContributorRole.TRANSLATOR);
        given(contributorRepository.save(any(Contributor.class)))
                .willReturn(saved);

        // when
        Long resultId =
                contributorService.findOrCreateContributor("새저자", ContributorRole.TRANSLATOR);

        // then
        assertThat(resultId).isEqualTo(20L);
        verify(contributorRepository, times(1))
                .findByContributorNameAndContributorRole("새저자", ContributorRole.TRANSLATOR);
        verify(contributorRepository, times(1)).save(any(Contributor.class));
    }

    @Test
    @DisplayName("findOrCreateContributor - 이름이 null 또는 공백이면 예외")
    void findOrCreateContributor_Fail_InvalidName() {
        // when & then
        assertThatThrownBy(() -> contributorService.findOrCreateContributor("   ", ContributorRole.AUTHOR))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("contributorName은 null일 수 없다.");

        verify(contributorRepository, never()).findByContributorNameAndContributorRole(anyString(), any());
        verify(contributorRepository, never()).save(any());
    }
}
