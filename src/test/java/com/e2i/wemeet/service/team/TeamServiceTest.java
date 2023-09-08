package com.e2i.wemeet.service.team;

import static com.e2i.wemeet.support.fixture.code.CodeFixture.INHA_UNIVERSITY;
import static com.e2i.wemeet.support.fixture.code.CodeFixture.SEOUL_UNIVERSITY;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.e2i.wemeet.domain.code.Code;
import com.e2i.wemeet.domain.code.CodePk;
import com.e2i.wemeet.domain.code.CodeRepository;
import com.e2i.wemeet.domain.member.Member;
import com.e2i.wemeet.domain.member.MemberRepository;
import com.e2i.wemeet.domain.member.data.Role;
import com.e2i.wemeet.domain.team.Team;
import com.e2i.wemeet.domain.team.TeamRepository;
import com.e2i.wemeet.domain.team.data.AdditionalActivity;
import com.e2i.wemeet.domain.team.data.DrinkRate;
import com.e2i.wemeet.domain.team.data.DrinkWithGame;
import com.e2i.wemeet.domain.team.data.Region;
import com.e2i.wemeet.domain.team_image.TeamImage;
import com.e2i.wemeet.domain.team_image.TeamImageRepository;
import com.e2i.wemeet.dto.request.team.CreateTeamRequestDto;
import com.e2i.wemeet.dto.request.team.UpdateTeamRequestDto;
import com.e2i.wemeet.dto.response.team.MyTeamResponseDto;
import com.e2i.wemeet.dto.response.team.TeamMemberResponseDto;
import com.e2i.wemeet.exception.badrequest.ProfileImageNotExistsException;
import com.e2i.wemeet.exception.badrequest.TeamExistsException;
import com.e2i.wemeet.exception.badrequest.TeamNotExistsException;
import com.e2i.wemeet.exception.unauthorized.UnAuthorizedUnivException;
import com.e2i.wemeet.service.aws.s3.S3Service;
import com.e2i.wemeet.support.fixture.MemberFixture;
import com.e2i.wemeet.support.fixture.TeamFixture;
import com.e2i.wemeet.support.fixture.TeamMemberFixture;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

@ExtendWith(MockitoExtension.class)
class TeamServiceTest {

    @Mock
    private CodeRepository codeRepository;

    @Mock
    private S3Service s3Service;

    @Mock
    private MemberRepository memberRepository;

    @Mock
    private TeamRepository teamRepository;

    @Mock
    private TeamImageRepository teamImageRepository;

    @InjectMocks
    private TeamServiceImpl teamService;

    @DisplayName("팀 생성 테스트")
    @Nested
    class Create {

        @DisplayName("팀 생성에 성공한다.")
        @Test
        void createTeam_Success() {
            // given
            Member teamLeader = MemberFixture.RIM.create_with_id(1L);
            CreateTeamRequestDto requestDto = TeamFixture.WOMAN_TEAM.createTeamRequestDto_2_members();
            List<MultipartFile> teamImages = List.of(
                new MockMultipartFile("test", "test".getBytes()));

            Code code = SEOUL_UNIVERSITY.create();
            CodePk codePk = CodePk.of(requestDto.members().get(0).collegeInfo().collegeCode());

            Team testTeam = TeamFixture.WOMAN_TEAM.create(MemberFixture.RIM.create(),
                TeamMemberFixture.create_1_woman());

            // when
            when(memberRepository.findById(anyLong())).thenReturn(Optional.of(teamLeader));
            when(codeRepository.findByCodePk(codePk))
                .thenReturn(Optional.of(code));
            when(codeRepository.findByCodePk(codePk))
                .thenReturn(Optional.of(code));
            when(teamRepository.save(any(Team.class))).thenReturn(testTeam);

            teamService.createTeam(1L, requestDto, teamImages);

            // then
            verify(codeRepository, times(requestDto.members().size())).findByCodePk(codePk);
            verify(teamRepository).save(any(Team.class));
            verify(teamImageRepository, times(teamImages.size())).save(any(TeamImage.class));
            assertThat(teamLeader.getRole()).isEqualTo(Role.MANAGER);
        }

        @DisplayName("이미 팀이 있는 경우 팀을 생성할 수 없다.")
        @Test
        void createTeam_ExistedTeam() {
            // given
            Member teamLeader = MemberFixture.RIM.create_with_id(1L);
            CreateTeamRequestDto requestDto = TeamFixture.WOMAN_TEAM.createTeamRequestDto_2_members();
            List<MultipartFile> teamImages = List.of(
                new MockMultipartFile("test", "test".getBytes()));

            TeamFixture.WOMAN_TEAM.create(teamLeader, TeamMemberFixture.create_3_woman());

            // when
            when(memberRepository.findById(anyLong())).thenReturn(Optional.of(teamLeader));

            // then
            assertThatThrownBy(() -> teamService.createTeam(1L, requestDto, teamImages))
                .isExactlyInstanceOf(TeamExistsException.class);
            verify(memberRepository).findById(1L);
        }

        @DisplayName("대학생 인증이 안된 사용자라면 팀을 생성할 수 없다.")
        @Test
        void createTeam_UncertifiedEmail() {
            // given
            Member teamLeader = MemberFixture.RIM.create_with_id(1L);
            CreateTeamRequestDto requestDto = TeamFixture.WOMAN_TEAM.createTeamRequestDto_2_members();
            List<MultipartFile> teamImages = List.of(
                new MockMultipartFile("test", "test".getBytes()));

            teamLeader.saveEmail(null);

            // when
            when(memberRepository.findById(anyLong())).thenReturn(Optional.of(teamLeader));

            // then
            assertThatThrownBy(() -> teamService.createTeam(1L, requestDto, teamImages))
                .isExactlyInstanceOf(UnAuthorizedUnivException.class);
            verify(memberRepository).findById(1L);
        }

        @DisplayName("사진 등록이 안된 사용자라면 팀을 생성할 수 없다.")
        @Test
        void createTeam_NotExistedProfileImage() {
            // given
            Member teamLeader = MemberFixture.RIM.create_with_id(1L);
            CreateTeamRequestDto requestDto = TeamFixture.WOMAN_TEAM.createTeamRequestDto_2_members();
            List<MultipartFile> teamImages = List.of(
                new MockMultipartFile("test", "test".getBytes()));

            teamLeader.saveProfileImage(null);

            // when
            when(memberRepository.findById(anyLong())).thenReturn(Optional.of(teamLeader));

            // then
            assertThatThrownBy(() -> teamService.createTeam(1L, requestDto, teamImages))
                .isExactlyInstanceOf(ProfileImageNotExistsException.class);
            verify(memberRepository).findById(1L);
        }
    }

    @DisplayName("팀 조회 테스트")
    @Nested
    class Read {

        @DisplayName("나의 팀 정보를 조회할 수 있다.")
        @Test
        void readTeam_Success() {
            // given
            Member teamLeader = MemberFixture.RIM.create_with_id(1L);
            Team team = TeamFixture.WOMAN_TEAM.create(teamLeader,
                TeamMemberFixture.create_1_woman());

            // when
            when(memberRepository.findById(anyLong())).thenReturn(Optional.of(teamLeader));

            MyTeamResponseDto response = teamService.readTeam(1L);

            // then
            verify(memberRepository).findById(1L);
            assertThat(response.hasTeam()).isTrue();
            assertThat(response.team()).isNotNull()
                .extracting("memberNum", "region", "drinkRate", "drinkWithGame",
                    "additionalActivity", "introduction", "members", "chatLink", "profileImageURL")
                .contains(team.getMemberNum(), team.getRegion().getName(),
                    team.getDrinkRate().getName(), team.getDrinkWithGame().getName(),
                    team.getAdditionalActivity().getName(), team.getIntroduction(),
                    List.of(TeamMemberResponseDto.of(TeamMemberFixture.OLIVIA.create())),
                    team.getChatLink(), teamLeader.getProfileImage().getLowUrl());
        }

        @DisplayName("팀이 없는 경우 나의 팀 정보를 조회할 수 없다.")
        @Test
        void readTeam_NotExistedTeam() {
            // given
            Member teamLeader = MemberFixture.RIM.create_with_id(1L);

            // when
            when(memberRepository.findById(anyLong())).thenReturn(Optional.of(teamLeader));

            MyTeamResponseDto response = teamService.readTeam(1L);

            // then
            verify(memberRepository).findById(1L);
            assertThat(response.hasTeam()).isFalse();
            assertThat(response.team()).isNull();
        }
    }

    @DisplayName("팀 수정 테스트")
    @Nested
    class Update {

        @DisplayName("나의 팀을 수정할 수 있다.")
        @Test
        void updateTeam_Success() {
            // given
            Member teamLeader = MemberFixture.RIM.create_with_id(1L);
            Team team = TeamFixture.WOMAN_TEAM.create(teamLeader,
                TeamMemberFixture.create_1_woman());

            UpdateTeamRequestDto requestDto = TeamFixture.WOMAN_TEAM_2.updateTeamRequestDto_2_members();
            List<MultipartFile> teamImages = List.of(
                new MockMultipartFile("test", "test".getBytes()));

            Code code = INHA_UNIVERSITY.create();
            CodePk codePk = CodePk.of(requestDto.members().get(0).collegeInfo().collegeCode());

            // when
            when(memberRepository.findById(anyLong())).thenReturn(Optional.of(teamLeader));
            when(codeRepository.findByCodePk(codePk))
                .thenReturn(Optional.of(code));
            when(codeRepository.findByCodePk(codePk))
                .thenReturn(Optional.of(code));

            teamService.updateTeam(1L, requestDto, teamImages);

            // then
            verify(memberRepository).findById(1L);
            verify(codeRepository, times(requestDto.members().size())).findByCodePk(codePk);
            verify(teamImageRepository, times(teamImages.size())).save(any(TeamImage.class));

            assertThat(team).isNotNull()
                .extracting("memberNum", "region", "drinkRate", "drinkWithGame",
                    "additionalActivity", "introduction")
                .contains(requestDto.members().size() + 1, Region.valueOf(requestDto.region()),
                    DrinkRate.valueOf(requestDto.drinkRate()),
                    DrinkWithGame.valueOf(requestDto.drinkWithGame()),
                    AdditionalActivity.valueOf(requestDto.additionalActivity()),
                    requestDto.introduction());
        }

        @DisplayName("팀이 없는 경우 팀 정보를 수정할 수 없다.")
        @Test
        void updateTeam_NotExistedTeam() {
            // given
            Member teamLeader = MemberFixture.RIM.create_with_id(1L);
            UpdateTeamRequestDto requestDto = TeamFixture.WOMAN_TEAM.updateTeamRequestDto_2_members();
            List<MultipartFile> teamImages = List.of(
                new MockMultipartFile("test", "test".getBytes()));

            // when
            when(memberRepository.findById(anyLong())).thenReturn(Optional.of(teamLeader));

            // then
            assertThatThrownBy(() -> teamService.updateTeam(1L, requestDto, teamImages))
                .isExactlyInstanceOf(TeamNotExistsException.class);
            verify(memberRepository).findById(1L);
        }
    }

    @DisplayName("팀 삭제 테스트")
    @Nested
    class Delete {

        @DisplayName("나의 팀을 삭제할 수 있다.")
        @Test
        void deleteTeam_Success() {
            // given
            Member teamLeader = MemberFixture.RIM.create_with_id(1L);
            Team team = TeamFixture.WOMAN_TEAM.create(teamLeader,
                TeamMemberFixture.create_1_woman());

            // when
            when(memberRepository.findById(anyLong())).thenReturn(Optional.of(teamLeader));

            teamService.deleteTeam(1L);

            // then
            verify(memberRepository).findById(1L);
            assertThat(team.getDeletedAt()).isNotNull();
            assertThat(teamLeader.getRole()).isEqualTo(Role.USER);
        }


        @DisplayName("생성한 팀이 없는 경우 팀을 삭제할 수 없다.")
        @Test
        void deleteTeam_NotExistedTeam() {
            // given
            Member teamLeader = MemberFixture.RIM.create_with_id(1L);

            // when
            when(memberRepository.findById(anyLong())).thenReturn(Optional.of(teamLeader));

            // then
            assertThatThrownBy(() -> teamService.deleteTeam(1L))
                .isExactlyInstanceOf(TeamNotExistsException.class);
            verify(memberRepository).findById(1L);
        }
    }
}
