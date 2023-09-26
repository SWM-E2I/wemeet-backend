package com.e2i.wemeet.service.member;

import static com.e2i.wemeet.support.fixture.MemberFixture.KAI;
import static com.e2i.wemeet.support.fixture.code.CodeFixture.SEOUL_UNIVERSITY;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.e2i.wemeet.domain.code.Code;
import com.e2i.wemeet.domain.code.CodePk;
import com.e2i.wemeet.domain.code.CodeRepository;
import com.e2i.wemeet.domain.member.Member;
import com.e2i.wemeet.domain.member.MemberRepository;
import com.e2i.wemeet.domain.member.data.Mbti;
import com.e2i.wemeet.dto.request.member.CreateMemberRequestDto;
import com.e2i.wemeet.dto.request.member.UpdateMemberRequestDto;
import com.e2i.wemeet.dto.response.member.MemberDetailResponseDto;
import com.e2i.wemeet.dto.response.member.MemberRoleResponseDto;
import com.e2i.wemeet.exception.badrequest.InvalidMbtiException;
import com.e2i.wemeet.exception.badrequest.MemberHasBeenDeletedException;
import com.e2i.wemeet.exception.notfound.CodeNotFoundException;
import com.e2i.wemeet.exception.notfound.MemberNotFoundException;
import com.e2i.wemeet.security.model.MemberPrincipal;
import com.e2i.wemeet.support.config.ReflectionUtils;
import java.time.LocalDateTime;
import java.util.Optional;
import org.hibernate.exception.DataException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataIntegrityViolationException;

@ExtendWith(MockitoExtension.class)
class MemberServiceTest {

    @Mock
    private MemberRepository memberRepository;

    @Mock
    private CodeRepository codeRepository;

    @InjectMocks
    private MemberServiceImpl memberService;

    @DisplayName("회원 가입 테스트")
    @Nested
    class Create {

        @DisplayName("회원 가입을 할 수 있다.")
        @Test
        void createMember_Success() {
            // given
            CreateMemberRequestDto requestDto = KAI.createMemberRequestDto();
            Code code = SEOUL_UNIVERSITY.create();
            Member entity = requestDto.toEntity(code);
            ReflectionUtils.setFieldValue(entity, "memberId", 1L);
            CodePk codePk = CodePk.of(requestDto.collegeInfo().collegeCode());

            // when
            when(codeRepository.findByCodePk(codePk))
                .thenReturn(Optional.of(code));
            when(memberRepository.save(any()))
                .thenReturn(entity);

            memberService.createMember(requestDto);

            // then
            verify(codeRepository).findByCodePk(codePk);
            verify(memberRepository).save(any(Member.class));
        }

        @DisplayName("동일한 번호로 가입한 사용자가 있다면, 회원 가입에 실패한다.")
        @Test
        void createMember_DuplicatedPhoneNumber() {
            // given
            CreateMemberRequestDto requestDto = KAI.createMemberRequestDto();
            Code code = SEOUL_UNIVERSITY.create();
            Member entity = requestDto.toEntity(code);
            ReflectionUtils.setFieldValue(entity, "memberId", 1L);
            CodePk codePk = CodePk.of(requestDto.collegeInfo().collegeCode());

            // when
            when(codeRepository.findByCodePk(codePk))
                .thenReturn(Optional.of(code));
            when(memberRepository.save(any()))
                .thenThrow(DataIntegrityViolationException.class);

            // then
            assertThatThrownBy(() -> memberService.createMember(requestDto))
                .isInstanceOf(DataIntegrityViolationException.class);

            verify(codeRepository).findByCodePk(codePk);
            verify(memberRepository).save(any(Member.class));
        }

        @DisplayName("College CODE 값이 잘못되었다면, 회원 가입에 실패한다.")
        @Test
        void createMember_InvalidCollegeCode() {
            // given
            final String invalidCollegeCode = "XX-999";
            CreateMemberRequestDto requestDto = KAI.createMemberRequestDto(invalidCollegeCode);

            Code code = SEOUL_UNIVERSITY.create();
            Member entity = requestDto.toEntity(code);
            CodePk codePk = CodePk.of(requestDto.collegeInfo().collegeCode());

            // when
            when(codeRepository.findByCodePk(codePk))
                .thenThrow(CodeNotFoundException.class);

            // then
            assertThatThrownBy(() -> memberService.createMember(requestDto))
                .isExactlyInstanceOf(CodeNotFoundException.class);

            verify(codeRepository).findByCodePk(codePk);
            verify(memberRepository, times(0)).save(any(Member.class));
        }
    }

    @DisplayName("회원 정보 수정 테스트")
    @Nested
    class Update {

        @DisplayName("회원의 닉네임과 MBTI 정보를 수정할 수 있다.")
        @Test
        void updateNickname() {
            // given
            Member kai = KAI.create_with_id(1L);
            UpdateMemberRequestDto updateRequest = new UpdateMemberRequestDto("기우미우", "ESTJ");
            when(memberRepository.findByMemberId(1L))
                .thenReturn(Optional.of(kai));

            // when
            memberService.updateMember(1L, updateRequest);

            // then
            verify(memberRepository).findByMemberId(1L);
            assertThat(kai.getNickname()).isEqualTo("기우미우");
            assertThat(kai.getMbti()).isEqualTo(Mbti.ESTJ);
        }

        @DisplayName("회원의 ID가 잘못되었다면 회원 정보를 수정할 수 없다.")
        @Test
        void updateWithInvalidId() {
            // given
            final Long invalidId = 999L;
            UpdateMemberRequestDto updateRequest = new UpdateMemberRequestDto("기우미우", "ESTJ");
            when(memberRepository.findByMemberId(invalidId))
                .thenThrow(MemberNotFoundException.class);

            // when & then
            assertThatThrownBy(() -> memberService.updateMember(invalidId, updateRequest))
                .isExactlyInstanceOf(MemberNotFoundException.class);
            verify(memberRepository).findByMemberId(invalidId);
        }

        @DisplayName("수정한 MBTI가 존재하지 않다면 MBTI를 수정할 수 없다.")
        @Test
        void updateWithInvalidMbti() {
            // given
            Member kai = KAI.create_with_id(1L);
            UpdateMemberRequestDto updateRequest = new UpdateMemberRequestDto("기우미우", "PAQS");
            when(memberRepository.findByMemberId(1L))
                .thenReturn(Optional.of(kai));

            // when & then
            assertThatThrownBy(() -> memberService.updateMember(1L, updateRequest))
                .isExactlyInstanceOf(InvalidMbtiException.class);
            verify(memberRepository).findByMemberId(1L);
        }

        @DisplayName("닉네임의 길이가 10자가 넘어간다면 닉네임을 수정할 수 없다.")
        @Test
        void updateWithOverTenLengthNickname() {
            // given
            final String overTenLengthNickname = "기우미우기우미우기우미";
            Member kai = KAI.create_with_id(1L);
            UpdateMemberRequestDto updateRequest = new UpdateMemberRequestDto(overTenLengthNickname,
                "ESTJ");
            when(memberRepository.findByMemberId(1L))
                .thenThrow(DataException.class);

            // when & then
            assertThatThrownBy(() -> memberService.updateMember(1L, updateRequest))
                .isExactlyInstanceOf(DataException.class);
            verify(memberRepository).findByMemberId(1L);
        }
    }

    @DisplayName("회원 정보 조회 테스트")
    @Nested
    class Read {

        @DisplayName("회원 정보 조회에 성공한다.")
        @Test
        void readMemberInfo_Success() {
            // given
            final Member kai = KAI.create_with_id(1L);

            // when
            when(memberRepository.findByIdFetchCode(1L))
                .thenReturn(Optional.of(kai));

            // then
            MemberDetailResponseDto memberDetailResponseDto = memberService.readMemberDetail(
                kai.getMemberId());
            assertThat(memberDetailResponseDto).isNotNull()
                .extracting("nickname", "college", "collegeType", "mbti", "admissionYear",
                    "authUnivStatus")
                .contains(kai.getNickname(), "안양대", "인문사회", Mbti.INFJ, "17", true);
        }

        @DisplayName("회원 ID가 잘못되었을 경우, 회원 정보를 조회할 수 없다.")
        @Test
        void readWithInvalidId() {
            // given
            final Long invalidId = 999L;

            // when
            when(memberRepository.findByIdFetchCode(999L))
                .thenReturn(Optional.empty());

            // then
            assertThatThrownBy(() -> memberService.readMemberDetail(invalidId))
                .isExactlyInstanceOf(MemberNotFoundException.class);
        }

        @DisplayName("삭제한 회원일 경우 회원 정보를 조회할 수 없다.")
        @Test
        void readWithDeletedMember() {
            // given
            final Member kai = KAI.create_with_id(1L);
            kai.delete(LocalDateTime.now());

            // when
            when(memberRepository.findByIdFetchCode(1L))
                .thenReturn(Optional.of(kai));

            // then
            assertThatThrownBy(() -> memberService.readMemberDetail(1L))
                .isExactlyInstanceOf(MemberHasBeenDeletedException.class);
        }

        @DisplayName("회원의 팀 소속 여부와 권한에 대해 조회할 수 있다.")
        @CsvSource(value = {"1, USER, false", "2, MANAGER, true"})
        @ParameterizedTest
        void readRole(Long memberId, String role, boolean isManager) {
            // given
            MemberPrincipal memberPrincipal = new MemberPrincipal(memberId, role);

            // when
            MemberRoleResponseDto responseDto = memberService.readMemberRole(memberPrincipal);

            // then
            assertThat(responseDto.isManager()).isEqualTo(isManager);
            assertThat(responseDto.hasTeam()).isEqualTo(isManager);
        }
    }

    @DisplayName("회원 탈퇴 테스트")
    @Nested
    class Delete {

        @DisplayName("회원 탈퇴를 할 수 있다.")
        @Test
        void delete() {
            // given
            Member kai = KAI.create_with_id(1L);

            // when
            when(memberRepository.findByMemberId(1L))
                .thenReturn(Optional.of(kai));
            LocalDateTime deletedTime = LocalDateTime.now();
            memberService.deleteMember(1L, deletedTime);

            // then
            assertThat(kai.getDeletedAt()).isEqualTo(deletedTime);
        }

        @DisplayName("회원 ID가 잘못되었을 경우, 회원 탈퇴를 할 수 없다.")
        @Test
        void deleteWithInvalidId() {
            // given
            final Long invalidId = 999L;

            // when
            when(memberRepository.findByMemberId(invalidId))
                .thenThrow(MemberNotFoundException.class);
            LocalDateTime deletedTime = LocalDateTime.now();

            // then
            assertThatThrownBy(() -> memberService.deleteMember(invalidId, deletedTime))
                .isExactlyInstanceOf(MemberNotFoundException.class);
        }

        @DisplayName("이미 탈퇴한 회원일 경우 회원 탈퇴를 할 수 없다.")
        @Test
        void deleteWithAlreadyDeleted() {
            // given
            Member kai = KAI.create_with_id(1L);
            ReflectionUtils.setFieldValue(kai, "deletedAt", LocalDateTime.now());

            // when
            when(memberRepository.findByMemberId(1L))
                .thenReturn(Optional.of(kai));

            LocalDateTime deletedTime = LocalDateTime.now();

            // then
            assertThatThrownBy(() -> memberService.deleteMember(1L, deletedTime))
                .isExactlyInstanceOf(MemberHasBeenDeletedException.class);
        }
    }
}
