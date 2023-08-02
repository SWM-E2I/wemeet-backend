package com.e2i.wemeet.service.team;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.e2i.wemeet.domain.code.Code;
import com.e2i.wemeet.domain.member.Member;
import com.e2i.wemeet.domain.member.MemberRepository;
import com.e2i.wemeet.domain.member.Role;
import com.e2i.wemeet.domain.profileimage.ProfileImageRepository;
import com.e2i.wemeet.domain.team.Team;
import com.e2i.wemeet.domain.team.TeamRepository;
import com.e2i.wemeet.domain.team.invitation.InvitationAcceptStatus;
import com.e2i.wemeet.domain.team.invitation.TeamInvitation;
import com.e2i.wemeet.domain.team.invitation.TeamInvitationRepository;
import com.e2i.wemeet.domain.teampreferencemeetingtype.TeamPreferenceMeetingTypeRepository;
import com.e2i.wemeet.dto.request.team.CreateTeamRequestDto;
import com.e2i.wemeet.dto.request.team.ModifyTeamRequestDto;
import com.e2i.wemeet.dto.response.team.MyTeamDetailResponseDto;
import com.e2i.wemeet.exception.badrequest.ManagerSelfDeletionException;
import com.e2i.wemeet.exception.badrequest.NonTeamMemberException;
import com.e2i.wemeet.exception.badrequest.NotBelongToTeamException;
import com.e2i.wemeet.exception.badrequest.TeamAlreadyExistsException;
import com.e2i.wemeet.exception.notfound.MemberNotFoundException;
import com.e2i.wemeet.exception.unauthorized.UnAuthorizedUnivException;
import com.e2i.wemeet.security.token.TokenInjector;
import com.e2i.wemeet.support.fixture.MemberFixture;
import com.e2i.wemeet.support.fixture.ProfileImageFixture;
import com.e2i.wemeet.support.fixture.TeamFixture;
import jakarta.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
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
    @Mock
    private TokenInjector tokenInjector;
    @Mock
    private HttpServletResponse response;
    @Mock
    private TeamPreferenceMeetingTypeRepository teamPreferenceMeetingTypeRepository;
    @Mock
    private TeamInvitationRepository teamInvitationRepository;

    @Mock
    private ProfileImageRepository profileImageRepository;

    @InjectMocks
    private TeamServiceImpl teamService;

    private Member member;
    private Member manager;
    private Member inviteMember;
    private Team team;
    private List<Code> preferenceMeetingTypeCode = new ArrayList<>();

    private Long managerId;
    private Long memberId;

    private TeamInvitation invitation;

    @BeforeEach
    void setUp() {
        member = MemberFixture.KAI.create();
        manager = MemberFixture.SEYUN.create();
        inviteMember = MemberFixture.JEONGYEOL.create();
        team = TeamFixture.TEST_TEAM.create_with_id(MemberFixture.KAI.create(), 1L);
        preferenceMeetingTypeCode = new ArrayList<>();
        managerId = manager.getMemberId();
        memberId = member.getMemberId();
        invitation = TeamInvitation.builder()
            .teamInvitationId(1L)
            .team(team)
            .member(inviteMember)
            .acceptStatus(InvitationAcceptStatus.WAITING)
            .build();

    }

    @DisplayName("팀 생성에 성공한다.")
    @Test
    void createTeam_Success() {
        // given
        CreateTeamRequestDto requestDto = TeamFixture.TEST_TEAM.createTeamRequestDto();

        when(memberRepository.findById(anyLong())).thenReturn(
            Optional.ofNullable(member));
        when(teamRepository.save(any(Team.class))).thenReturn(team);
        when(teamPreferenceMeetingTypeRepository.saveAll(anyList())).thenReturn(new ArrayList<>());

        // when
        teamService.createTeam(1L, requestDto, preferenceMeetingTypeCode, response);

        // then
        verify(memberRepository).findById(anyLong());
        verify(teamRepository).save(any(Team.class));
        verify(teamPreferenceMeetingTypeRepository).saveAll(anyList());

        assertThat(member.getRole()).isEqualTo(Role.MANAGER);
    }

    @DisplayName("회원이 존재하지 않는 경우 팀 생성을 요청하면 MemberNotFoundException이 발생한다.")
    @Test
    void createTeam_NotFoundMember() {
        // given
        CreateTeamRequestDto requestDto = TeamFixture.TEST_TEAM.createTeamRequestDto();
        when(memberRepository.findById(anyLong())).thenReturn(Optional.empty());

        // when & then
        assertThatThrownBy(
            () -> teamService.createTeam(1L, requestDto, preferenceMeetingTypeCode,
                response)).isInstanceOf(MemberNotFoundException.class);

        verify(memberRepository).findById(anyLong());
        verify(teamRepository, never()).save(any(Team.class));
    }

    @DisplayName("이미 소속된 팀이 있는 경우 팀 생성을 요청하면 TeamAlreadyExistsException이 발생한다.")
    @Test
    void createTeam_TeamAlreadyExists() {
        // given
        CreateTeamRequestDto requestDto = TeamFixture.TEST_TEAM.createTeamRequestDto();
        when(memberRepository.findById(anyLong())).thenReturn(Optional.ofNullable(member));

        member.setTeam(team);

        // when & then
        assertThatThrownBy(
            () -> teamService.createTeam(1L, requestDto, preferenceMeetingTypeCode, response))
            .isInstanceOf(TeamAlreadyExistsException.class);

        verify(memberRepository).findById(anyLong());
        verify(teamRepository, never()).save(any(Team.class));

        // after
        member.setTeam(null);
    }

    @DisplayName("대학 미인증 사용자인 경우 팀 생성을 요청하면 UnAuthorizedUnivException이 발생한다.")
    @Test
    void createTeam_UnAuthorizedUniv() {
        // given
        CreateTeamRequestDto requestDto = TeamFixture.TEST_TEAM.createTeamRequestDto();
        when(memberRepository.findById(anyLong())).thenReturn(Optional.ofNullable(member));

        member.getCollegeInfo().saveMail(null);

        // when & then
        assertThatThrownBy(
            () -> teamService.createTeam(1L, requestDto, preferenceMeetingTypeCode, response))
            .isInstanceOf(UnAuthorizedUnivException.class);

        verify(memberRepository).findById(anyLong());
        verify(teamRepository, never()).save(any(Team.class));

        // after
        member.getCollegeInfo().saveMail("test@test.ac.kr");
    }

    @DisplayName("팀 수정에 성공한다.")
    @Test
    void modifyTeam_Success() {
        // given
        ModifyTeamRequestDto requestDto = TeamFixture.TEST_TEAM.modifyTeamRequestDto();
        member.setTeam(team);

        when(memberRepository.findById(anyLong())).thenReturn(
            Optional.ofNullable(member));
        when(teamPreferenceMeetingTypeRepository.saveAll(anyList())).thenReturn(new ArrayList<>());

        // when
        teamService.modifyTeam(1L, requestDto, preferenceMeetingTypeCode);

        // then
        verify(memberRepository).findById(anyLong());
        verify(teamPreferenceMeetingTypeRepository).saveAll(anyList());

        assertThat(team.getRegion()).isEqualTo(requestDto.region());
        assertThat(team.getDrinkingOption()).isEqualTo(requestDto.drinkingOption());
        assertThat(team.getAdditionalActivity()).hasToString(
            requestDto.additionalActivity());
        assertThat(team.getIntroduction()).isEqualTo(requestDto.introduction());

        // after
        member.setTeam(null);
    }

    @DisplayName("회원이 존재하지 않는 경우 팀 정보 수정을 요청하면 MemberNotFoundException이 발생한다.")
    @Test
    void modifyTeam_NotFoundMember() {
        // given
        ModifyTeamRequestDto requestDto = TeamFixture.TEST_TEAM.modifyTeamRequestDto();
        when(memberRepository.findById(anyLong())).thenReturn(Optional.empty());

        // when & then
        assertThatThrownBy(
            () -> teamService.modifyTeam(1L, requestDto, preferenceMeetingTypeCode))
            .isInstanceOf(MemberNotFoundException.class);

        verify(memberRepository).findById(anyLong());
        verify(teamPreferenceMeetingTypeRepository, never()).saveAll(anyList());
    }

    @DisplayName("소속 팀이 있는 경우 마이 팀 조회에 성공한다.")
    @Test
    void getMyTeamWithExistTeam_Success() {
        // given
        member.setTeam(team);

        when(memberRepository.findById(anyLong()))
            .thenReturn(Optional.ofNullable(member));
        when(teamPreferenceMeetingTypeRepository.findByTeamTeamId(anyLong()))
            .thenReturn(new ArrayList<>());

        // when
        MyTeamDetailResponseDto result = teamService.getMyTeamDetail(1L);

        // then
        assertThat(team.getMemberCount()).isEqualTo(result.memberCount());
        assertThat(team.getRegion()).isEqualTo(result.region());
        assertThat(team.getDrinkingOption()).isEqualTo(result.drinkingOption());
        assertThat(team.getAdditionalActivity()).isEqualTo(
            result.additionalActivity());
        assertThat(team.getIntroduction()).isEqualTo(result.introduction());
        assertThat(team.getTeamLeader().getImageAuth()).isEqualTo(
            result.managerImageAuth());

        // after
        member.setTeam(null);
    }

    @DisplayName("소속 팀이 없는 경우 마이 팀을 조회하면 null이 반환된다.")
    @Test
    void getMyTeamWithNotExistTeam_Success() {
        // given
        member.setTeam(null);
        when(memberRepository.findById(anyLong())).thenReturn(
            Optional.ofNullable(member));

        // when
        MyTeamDetailResponseDto teamDetailResponseDto = teamService.getMyTeamDetail(1L);

        // then
        verify(memberRepository).findById(anyLong());
        verify(teamPreferenceMeetingTypeRepository, never()).findByTeamTeamId(anyLong());
        assertThat(member.getTeam()).isNull();
        assertThat(teamDetailResponseDto).isNull();
    }

    @DisplayName("회원이 존재하지 않는 경우 팀 정보 수정을 요청하면 MemberNotFoundException이 발생한다.")
    @Test
    void getMyTeam_NotFoundMember() {
        // given
        when(memberRepository.findById(anyLong())).thenReturn(Optional.empty());

        // when & then
        assertThatThrownBy(
            () -> teamService.getMyTeamDetail(1L))
            .isInstanceOf(MemberNotFoundException.class);

        verify(memberRepository).findById(anyLong());
        verify(teamPreferenceMeetingTypeRepository, never()).findByTeamTeamId(anyLong());
    }


    @DisplayName("팀원 조회에 성공한다.")
    @Test
    void getTeamMemberList_Success() {
        // given
        manager.setTeam(team);
        when(memberRepository.findById(manager.getMemberId())).thenReturn(
            Optional.of(manager));

        when(teamInvitationRepository.findByTeamTeamIdAndAcceptStatus(
            team.getTeamId(), InvitationAcceptStatus.WAITING)).thenReturn(
            List.of(invitation));
        when(profileImageRepository.findByMemberMemberIdAndIsMain(inviteMember.getMemberId(),
            true)).thenReturn(Optional.of(ProfileImageFixture.MAIN_IMAGE.create()));

        // when
        teamService.getTeamMemberList(manager.getMemberId());

        // then
        verify(memberRepository).findById(manager.getMemberId());
        verify(teamInvitationRepository).findByTeamTeamIdAndAcceptStatus(
            team.getTeamId(), InvitationAcceptStatus.WAITING);
    }

    @DisplayName("팀이 없는 경우 팀원 조회를 하면 NotBelongToTeamException이 발생한다.")
    @Test
    void getTeamMemberList_NotBelongToTeamException() {
        // given
        manager.setTeam(null);
        when(memberRepository.findById(managerId)).thenReturn(
            Optional.of(manager));

        // when & then
        assertThatThrownBy(
            () -> teamService.getTeamMemberList(managerId))
            .isInstanceOf(NotBelongToTeamException.class);

        verify(memberRepository).findById(managerId);
        verify(teamInvitationRepository, never()).findByTeamTeamIdAndAcceptStatus(
            team.getTeamId(), InvitationAcceptStatus.WAITING);
    }

    @DisplayName("팀 삭제에 성공한다.")
    @Test
    void deleteTeam_Success() {
        // given
        when(memberRepository.findById(managerId)).thenReturn(
            Optional.of(manager));
        manager.setTeam(team);

        // when
        teamService.deleteTeam(managerId);

        // then
        verify(memberRepository).findById(managerId);
        assertThat(manager.getRole()).isEqualTo(Role.USER);

        // after
        manager.setRole(Role.MANAGER);
    }

    @DisplayName("팀을 삭제하면 팀원들의 팀 정보가 null 로 초기화되며 deleteAt에 삭제 시간이 기록된다.")
    @Test
    void deleteTeam_mark() {
        // given
        Team team = TeamFixture.HONGDAE_TEAM.create(manager);
        Member member = MemberFixture.JEONGYEOL.create();
        member.joinTeam(team);

        when(memberRepository.findById(managerId))
            .thenReturn(Optional.of(manager));

        // when
        teamService.deleteTeam(managerId);

        // then
        verify(memberRepository).findById(managerId);
        assertThat(member.getTeam()).isNull();
        assertThat(team.getMembers()).isEmpty();
        assertThat(team.getDeletedAt())
            .isNotNull()
            .isExactlyInstanceOf(java.time.LocalDateTime.class);
    }

    @DisplayName("팀이 없는 경우 팀 삭제를 요청하면 NotBelongToTeamException이 발생한다.")
    @Test
    void deleteTeam_NotBelongToTeamException() {
        // given
        manager.setTeam(null);
        when(memberRepository.findById(managerId)).thenReturn(
            Optional.of(manager));

        // when & then
        assertThatThrownBy(
            () -> teamService.deleteTeam(managerId))
            .isInstanceOf(NotBelongToTeamException.class);

        verify(memberRepository).findById(managerId);
        verify(teamRepository, never()).delete(any(Team.class));
    }

    @DisplayName("팀원 삭제에 성공한다.")
    @Test
    void deleteTeamMember_Success() {
        // given
        when(memberRepository.findById(manager.getMemberId())).thenReturn(
            Optional.of(manager));
        when(memberRepository.findById(memberId)).thenReturn(
            Optional.of(member));
        manager.setTeam(team);
        team.addMember(member);

        // when
        teamService.deleteTeamMember(manager.getMemberId(), memberId);

        // then
        verify(memberRepository).findById(managerId);
        verify(memberRepository).findById(memberId);
        assertThat(member.getTeam()).isNull();
    }

    @DisplayName("팀이 없는 경우 팀원 삭제를 요청하면 NotBelongToTeamException이 발생한다.")
    @Test
    void deleteTeamMember_NotBelongToTeamException() {
        // given
        when(memberRepository.findById(managerId)).thenReturn(
            Optional.of(manager));
        when(memberRepository.findById(memberId)).thenReturn(
            Optional.of(member));
        manager.setTeam(null);

        // when
        assertThatThrownBy(
            () -> teamService.deleteTeamMember(managerId, memberId))
            .isInstanceOf(NotBelongToTeamException.class);

        // then
        verify(memberRepository).findById(managerId);
        verify(memberRepository).findById(managerId);
        assertThat(manager.getTeam()).isNull();

        // after
        manager.setTeam(team);
    }

    @DisplayName("팀장 자신을 팀에서 삭제하려 하면 ManagerSelfDeletionException이 발생한다.")
    @Test
    void deleteTeamMember_ManagerSelfDeletionException() {
        // given & when & then
        assertThatThrownBy(
            () -> teamService.deleteTeamMember(managerId, managerId))
            .isInstanceOf(ManagerSelfDeletionException.class);

        verify(memberRepository, never()).findById(managerId);
        verify(memberRepository, never()).findById(memberId);
    }

    @DisplayName("다른 팀의 팀원을 삭제하려 하면 NonTeamMemberException이 발생한다.")
    @Test
    void deleteTeamMember_NonTeamMemberException() {
        // given
        when(memberRepository.findById(managerId)).thenReturn(
            Optional.of(manager));
        when(memberRepository.findById(memberId)).thenReturn(
            Optional.of(member));
        manager.setTeam(team);
        member.setTeam(null);

        // when & then
        assertThatThrownBy(
            () -> teamService.deleteTeamMember(managerId, memberId))
            .isInstanceOf(NonTeamMemberException.class);

        verify(memberRepository).findById(managerId);
        verify(memberRepository).findById(memberId);
    }
}
