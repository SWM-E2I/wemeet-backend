package com.e2i.wemeet.service.team;

import com.e2i.wemeet.domain.member.MemberRepository;
import com.e2i.wemeet.domain.team.TeamRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class TeamServiceTest {

    @Mock
    private MemberRepository memberRepository;

    @Mock
    private TeamRepository teamRepository;

    @InjectMocks
    private TeamServiceImpl teamService;

    @DisplayName("팀 생성 테스트")
    @Nested
    class Create {

        @DisplayName("팀 생성에 성공한다.")
        @Test
        void createTeam_Success() {
            // given

            // when

            // then
        }

        @DisplayName("이미 팀이 있는 경우 팀을 생성할 수 없다.")
        @Test
        void createTeam_ExistedTeam() {
            // given

            // when

            // then
        }

        @DisplayName("대학생 인증이 안된 사용자라면 팀을 생성할 수 없다.")
        @Test
        void createTeam_UncertifiedEmail() {
            // given

            // when

            // then
        }

        @DisplayName("사진 등록이 안된 사용자라면 팀을 생성할 수 없다.")
        @Test
        void createTeam_NotExistedProfileImage() {
            // given

            // when

            // then
        }

        @DisplayName("존재하지 않는 MBTI의 경우 팀을 생성할 수 없다.")
        @Test
        void createTeam_InvalidMbti() {
            // given

            // when

            // then
        }

        @DisplayName("존재하지 않는 대학교의 경우 팀을 생성할 수 없다.")
        @Test
        void createTeam_InvalidCollege() {
            // given

            // when

            // then
        }

        @DisplayName("존재하지 않는 학과 정보일 경우 팀을 생성할 수 없다.")
        @Test
        void createTeam_InvalidCollegeType() {
            // given

            // when

            // then
        }

        @DisplayName("팀 소개가 150자가 넘어간다면 팀을 생성할 수 없다.")
        @Test
        void createTeam_OverLengthIntroduction() {
            // given

            // when

            // then
        }

        @DisplayName("0 미만의 값으로 음주 수치를 입력한 경우 팀을 생성할 수 없다.")
        @Test
        void createTeam_UnderValueDrinkRate() {
            // given

            // when

            // then
        }

        @DisplayName("100을 초과하는 값으로 음주 수치를 입력한 경우 팀을 생성할 수 없다.")
        @Test
        void createTeam_OverValueDrinkRate() {
            // given

            // when

            // then
        }

        @DisplayName("존재하지 않는 술게임 여부 값의 경우 팀을 생성할 수 없다.")
        @Test
        void createTeam_InvalidDrinkWithGame() {
            // given

            // when

            // then
        }

        @DisplayName("존재하지 않는 추가 활동 값의 경우 팀을 생성할 수 없다.")
        @Test
        void createTeam_InvalidAdditionalActivity() {
            // given

            // when

            // then
        }
    }

    @DisplayName("팀 조회 테스트")
    @Nested
    class Read {

        @DisplayName("나의 팀 정보를 조회할 수 있다.")
        @Test
        void readTeam_Success() {
            // given

            // when

            // then
        }

        @DisplayName("팀이 없는 경우 조회할 수 있는 데이터가 없다.")
        @Test
        void readTeam_NotExistedTeam() {
            // given

            // when

            // then
        }
    }

    @DisplayName("팀 수정 테스트")
    @Nested
    class Update {

        @DisplayName("나의 팀을 수정할 수 있다.")
        @Test
        void updateTeam_Success() {
            // given

            // when

            // then
        }

        @DisplayName("나의 팀이 아닌 경우 수정할 수 없다.")
        @Test
        void updateTeam_NotMyTeam() {
            // given

            // when

            // then
        }

        @DisplayName("존재하지 않는 MBTI의 경우 팀 멤버 정보를 수정할 수 없다.")
        @Test
        void updateTeam_InvalidMbti() {
            // given

            // when

            // then
        }

        @DisplayName("존재하지 않는 대학교의 경우 팀 멤버 정보를 수정할 수 없다.")
        @Test
        void updateTeam_InvalidCollege() {
            // given

            // when

            // then
        }

        @DisplayName("존재하지 않는 학과 정보일 경우 팀 멤버 정보를 수정할 수 없다.")
        @Test
        void updateTeam_InvalidCollegeType() {
            // given

            // when

            // then
        }

        @DisplayName("팀 소개가 150자가 넘어간다면 팀 소개를 수정할 수 없다.")
        @Test
        void updateTeam_OverLengthIntroduction() {
            // given

            // when

            // then
        }

        @DisplayName("0 미만의 값으로 음주 수치를 수정할 수 없다.")
        @Test
        void updateTeam_UnderValueDrinkRate() {
            // given

            // when

            // then
        }

        @DisplayName("100을 초과하는 값으로 음주 수치를 수정할 수 없다.")
        @Test
        void updateTeam_OverValueDrinkRate() {
            // given

            // when

            // then
        }

        @DisplayName("존재하지 않는 술게임 여부 값의 경우 수정할 수 없다.")
        @Test
        void updateTeam_InvalidDrinkWithGame() {
            // given

            // when

            // then
        }

        @DisplayName("존재하지 않는 추가 활동 값의 경우 수정할 수 없다.")
        @Test
        void updateTeam_InvalidAdditionalActivity() {
            // given

            // when

            // then
        }
    }

    @DisplayName("팀 삭제 테스트")
    @Nested
    class Delete {

        @DisplayName("나의 팀을 삭제할 수 있다.")
        @Test
        void deleteTeam_Success() {
            // given

            // when

            // then
        }


        @DisplayName("생성한 팀이 없는 경우 팀을 삭제할 수 없다.")
        @Test
        void deleteTeam_NotExistedTeam() {
            // given

            // when

            // then
        }

        @DisplayName("본인이 생성한 팀이 아닌 경우 팀을 삭제할 수 없다.")
        @Test
        void deleteTeam_NotMyTeam() {
            // given

            // when

            // then
        }
    }
}
