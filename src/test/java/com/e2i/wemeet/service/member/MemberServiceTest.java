package com.e2i.wemeet.service.member;

import com.e2i.wemeet.domain.member.MemberRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class MemberServiceTest {

    @Mock
    private MemberRepository memberRepository;

    @InjectMocks
    private MemberServiceImpl memberService;

    @DisplayName("회원 가입 테스트")
    @Nested
    class Create {

        @DisplayName("회원 가입을 할 수 있다.")
        @Test
        void createMember_Success() {
            // given

            // when

            // then

        }

        @DisplayName("동일한 번호로 가입한 사용자가 있다면, 회원 가입에 실패한다.")
        @Test
        void createMember_DuplicatedPhoneNumber() {
            // given

            // when

            // then

        }

        @DisplayName("동일한 이메일로 가입한 사용자가 있다면, 회원 가입에 실패한다.")
        @Test
        void createMember_DuplicatedEmail() {
            // given

            // when

            // then

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
