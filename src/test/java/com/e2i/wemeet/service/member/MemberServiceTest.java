package com.e2i.wemeet.service.member;

import static com.e2i.wemeet.support.fixture.MemberFixture.KAI;
import static com.e2i.wemeet.support.fixture.code.CodeFixture.SEOUL_UNIVERSITY;
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
import com.e2i.wemeet.dto.request.member.CreateMemberRequestDto;
import com.e2i.wemeet.exception.notfound.CodeNotFoundException;
import com.e2i.wemeet.support.config.ReflectionUtils;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
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
            final String invalidCollegeCode = "E2I-MASTER";
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

        @DisplayName("회원의 닉네임 정보를 수정할 수 있다.")
        @Test
        void updateNickname() {
            // given

            // when

            // then

        }

        @DisplayName("회원의 MBIT 정보를 수정할 수 있다.")
        @Test
        void updateMbti() {
            // given

            // when

            // then

        }

        @DisplayName("회원의 ID가 잘못되었다면 회원 정보를 수정할 수 없다.")
        @Test
        void updateWithInvalidId() {
            // given

            // when

            // then

        }

        @DisplayName("수정한 MBTI가 존재하지 않다면 MBTI를 수정할 수 없다.")
        @Test
        void updateWithInvalidMbti() {
            // given

            // when

            // then

        }

        @DisplayName("닉네임의 길이가 20자가 넘어간다면 닉네임을 수정할 수 없다.")
        @Test
        void updateWithOverTwentyLengthNickname() {
            // given

            // when

            // then

        }
    }

    @DisplayName("회원 정보 조회 테스트")
    @Nested
    class Read {

        @DisplayName("회원 정보 조회에 성공한다.")
        @Test
        void readMemberInfo_Success() {
            // given

            // when

            // then

        }

        @DisplayName("회원 ID가 잘못되었을 경우, 회원 정보 조회에 실패한다.")
        @Test
        void readWithInvalidId() {
            // given

            // when

            // then
        }

        @DisplayName("삭제한 회원일 경우 회원 정보를 조회할 수 없다.")
        @Test
        void readWithDeletedMember() {
            // given

            // when

            // then
        }

        @DisplayName("회원의 팀 소속 여부와 권한에 대해 조회할 수 있다.")
        @Test
        void readRole() {
            // given

            // when

            // then
        }

        @DisplayName("회원의 일부 정보를 조회할 수 있다.")
        @Test
        void readMemberInfo() {
            // given

            // when

            // then
        }

        @DisplayName("회원의 상세 정보를 조회할 수 있다.")
        @Test
        void readMemberDetail() {
            // given

            // when

            // then
        }
    }

    @DisplayName("회원 탈퇴 테스트")
    @Nested
    class Delete {

        @DisplayName("회원 탈퇴를 할 수 있다.")
        @Test
        void delete() {
            // given

            // when

            // then

        }

        @DisplayName("회원 ID가 잘못되었을 경우, 회원 탈퇴를 할 수 없다.")
        @Test
        void deleteWithInvalidId() {
            // given

            // when

            // then

        }

        @DisplayName("이미 탈퇴한 회원일 경우 회원 탈퇴를 할 수 없다.")
        @Test
        void deleteWithAlreadyDeleted() {
            // given

            // when

            // then

        }
    }
}
