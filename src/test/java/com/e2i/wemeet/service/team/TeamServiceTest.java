package com.e2i.wemeet.service.team;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.e2i.wemeet.config.security.model.MemberPrincipal;
import com.e2i.wemeet.config.security.token.TokenInjector;
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
import com.e2i.wemeet.dto.request.team.InviteTeamRequestDto;
import com.e2i.wemeet.dto.request.team.ModifyTeamRequestDto;
import com.e2i.wemeet.dto.response.team.MyTeamDetailResponseDto;
import com.e2i.wemeet.exception.badrequest.GenderNotMatchException;
import com.e2i.wemeet.exception.badrequest.InvitationAlreadyExistsException;
import com.e2i.wemeet.exception.badrequest.ManagerSelfDeletionException;
import com.e2i.wemeet.exception.badrequest.NonTeamMemberException;
import com.e2i.wemeet.exception.badrequest.NotBelongToTeamException;
import com.e2i.wemeet.exception.badrequest.TeamAlreadyActiveException;
import com.e2i.wemeet.exception.badrequest.TeamAlreadyExistsException;
import com.e2i.wemeet.exception.notfound.InvitationNotFoundException;
import com.e2i.wemeet.exception.notfound.MemberNotFoundException;
import com.e2i.wemeet.exception.unauthorized.UnAuthorizedUnivException;
import com.e2i.wemeet.support.fixture.MemberFixture;
import com.e2i.wemeet.support.fixture.ProfileImageFixture;
import com.e2i.wemeet.support.fixture.TeamFixture;
import jakarta.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
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

    private static final Member member = MemberFixture.KAI.create();
    private static final Member manager = MemberFixture.SEYUN.create();
    private static final Member inviteMember = MemberFixture.JEONGYEOL.create();
    private static final Team team = TeamFixture.TEST_TEAM.create();
    private static final List<Code> preferenceMeetingTypeCode = new ArrayList<>();

    private static final Long managerId = manager.getMemberId();
    private static final Long memberId = member.getMemberId();

    private static final TeamInvitation invitation = TeamInvitation.builder()
        .teamInvitationId(1L)
        .team(team)
        .member(inviteMember)
        .acceptStatus(InvitationAcceptStatus.WAITING)
        .build();

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

        verify(tokenInjector).injectToken(any(HttpServletResponse.class),
            any(MemberPrincipal.class));

        assertEquals(Role.MANAGER, member.getRole());
    }

    @DisplayName("회원이 존재하지 않는 경우 팀 생성을 요청하면 MemberNotFoundException이 발생한다.")
    @Test
    void createTeam_NotFoundMember() {
        // given
        CreateTeamRequestDto requestDto = TeamFixture.TEST_TEAM.createTeamRequestDto();
        when(memberRepository.findById(anyLong())).thenReturn(Optional.empty());

        // when & then
        assertThrows(MemberNotFoundException.class, () -> {
            teamService.createTeam(1L, requestDto, preferenceMeetingTypeCode, response);
        });

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
        assertThrows(TeamAlreadyExistsException.class, () -> {
            teamService.createTeam(1L, requestDto, preferenceMeetingTypeCode, response);
        });

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
        assertThrows(UnAuthorizedUnivException.class, () -> {
            teamService.createTeam(1L, requestDto, preferenceMeetingTypeCode, response);
        });

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

        assertEquals(requestDto.region(), team.getRegion());
        assertEquals(requestDto.drinkingOption(), team.getDrinkingOption());
        assertEquals(requestDto.additionalActivity(), team.getAdditionalActivity().toString());
        assertEquals(requestDto.introduction(), team.getIntroduction());

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
        assertThrows(MemberNotFoundException.class, () -> {
            teamService.modifyTeam(1L, requestDto, preferenceMeetingTypeCode);
        });

        verify(memberRepository).findById(anyLong());
        verify(teamPreferenceMeetingTypeRepository, never()).saveAll(anyList());
    }

    @DisplayName("소속 팀이 있는 경우 마이 팀 조회에 성공한다.")
    @Test
    void getMyTeamWithExistTeam_Success() {
        // given
        member.setTeam(team);

        when(memberRepository.findById(anyLong())).thenReturn(
            Optional.ofNullable(member));
        when(teamPreferenceMeetingTypeRepository.findByTeamTeamId(anyLong())).thenReturn(
            new ArrayList<>());

        // when
        MyTeamDetailResponseDto result = teamService.getMyTeamDetail(1L);

        // then
        assertEquals(result.memberCount(), team.getMemberCount());
        assertEquals(result.region(), team.getRegion());
        assertEquals(result.drinkingOption(), team.getDrinkingOption());
        assertEquals(result.additionalActivity(), team.getAdditionalActivity());
        assertEquals(result.introduction(), team.getIntroduction());
        assertEquals(result.managerImageAuth(), team.getMember().isImageAuth());

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
        assertNull(member.getTeam());
        assertNull(teamDetailResponseDto);
    }

    @DisplayName("회원이 존재하지 않는 경우 팀 정보 수정을 요청하면 MemberNotFoundException이 발생한다.")
    @Test
    void getMyTeam_NotFoundMember() {
        // given
        when(memberRepository.findById(anyLong())).thenReturn(Optional.empty());

        // when & then
        assertThrows(MemberNotFoundException.class, () -> {
            teamService.getMyTeamDetail(1L);
        });

        verify(memberRepository).findById(anyLong());
        verify(teamPreferenceMeetingTypeRepository, never()).findByTeamTeamId(anyLong());
    }


    @DisplayName("팀 초대 신청에 성공한다.")
    @Test
    void inviteTeam_Success() {
        // given
        InviteTeamRequestDto requestDto = MemberFixture.KAI.inviteTeamRequestDto();
        manager.setTeam(team);
        member.setTeam(null);

        when(memberRepository.findByNicknameAndMemberCode(member.getNickname(),
            member.getMemberCode())).thenReturn(
            Optional.of(member));
        when(memberRepository.findById(manager.getMemberId())).thenReturn(Optional.of(manager));
        when(teamInvitationRepository.findByMemberMemberIdAndTeamTeamIdAndAcceptStatus(
            memberId, team.getTeamId(), InvitationAcceptStatus.WAITING)).thenReturn(
            Optional.empty());

        // when
        teamService.inviteTeam(manager.getMemberId(), requestDto);

        // then
        verify(memberRepository).findByNicknameAndMemberCode(member.getNickname(),
            member.getMemberCode());
        verify(memberRepository).findById(manager.getMemberId());
        verify(teamInvitationRepository).findByMemberMemberIdAndTeamTeamIdAndAcceptStatus(
            memberId, team.getTeamId(), InvitationAcceptStatus.WAITING);
        verify(teamInvitationRepository).save(any(TeamInvitation.class));
    }

    @DisplayName("nickname과 memberCode를 가진 사용자가 없는 경우 팀 초대 신청을 보내면 MemberNotFoundException이 발생한다.")
    @Test
    void inviteTeam_MemberNotFoundException() {
        // given
        InviteTeamRequestDto requestDto = MemberFixture.KAI.inviteTeamRequestDto();

        when(memberRepository.findByNicknameAndMemberCode(member.getNickname(),
            member.getMemberCode())).thenReturn(
            Optional.empty());

        // when & then
        assertThrows(MemberNotFoundException.class, () -> {
            teamService.inviteTeam(managerId, requestDto);
        });

        verify(memberRepository).findByNicknameAndMemberCode(member.getNickname(),
            member.getMemberCode());
        verify(memberRepository, never()).findById(manager.getMemberId());
        verify(teamInvitationRepository, never()).findByMemberMemberIdAndTeamTeamIdAndAcceptStatus(
            memberId, team.getTeamId(), InvitationAcceptStatus.WAITING);
        verify(teamInvitationRepository, never()).save(any(TeamInvitation.class));
    }

    @DisplayName("팀이 활성화된 상태에서 팀 초대 신청을 보내면 TeamAlreadyActiveException이 발생한다.")
    @Test
    void inviteTeam_TeamAlreadyActiveException() {
        // given
        InviteTeamRequestDto requestDto = MemberFixture.KAI.inviteTeamRequestDto();
        manager.setTeam(team);
        when(memberRepository.findByNicknameAndMemberCode(member.getNickname(),
            member.getMemberCode())).thenReturn(
            Optional.of(member));
        when(teamInvitationRepository.findByMemberMemberIdAndTeamTeamIdAndAcceptStatus(
            memberId, team.getTeamId(), InvitationAcceptStatus.WAITING)).thenReturn(
            Optional.empty());
        when(memberRepository.findById(manager.getMemberId())).thenReturn(Optional.of(manager));
        team.setActive(true);

        // when & then
        assertThrows(TeamAlreadyActiveException.class, () -> {
            teamService.inviteTeam(managerId, requestDto);
        });

        verify(memberRepository).findByNicknameAndMemberCode(member.getNickname(),
            member.getMemberCode());
        verify(memberRepository).findById(manager.getMemberId());
        verify(teamInvitationRepository).findByMemberMemberIdAndTeamTeamIdAndAcceptStatus(
            memberId, team.getTeamId(), InvitationAcceptStatus.WAITING);
        verify(teamInvitationRepository, never()).save(any(TeamInvitation.class));

        // after
        team.setActive(false);
    }

    @DisplayName("초대한 사용자에게 이미 소속 팀이 있는 경우 TeamAlreadyExistsException이 발생한다.")
    @Test
    void inviteTeam_TeamAlreadyExistsException() {
        // given
        InviteTeamRequestDto requestDto = MemberFixture.KAI.inviteTeamRequestDto();
        manager.setTeam(team);
        when(memberRepository.findByNicknameAndMemberCode(member.getNickname(),
            member.getMemberCode())).thenReturn(
            Optional.of(member));
        when(teamInvitationRepository.findByMemberMemberIdAndTeamTeamIdAndAcceptStatus(
            memberId, team.getTeamId(), InvitationAcceptStatus.WAITING)).thenReturn(
            Optional.empty());
        when(memberRepository.findById(manager.getMemberId())).thenReturn(Optional.of(manager));
        member.setTeam(team);

        // when & then
        assertThrows(TeamAlreadyExistsException.class, () -> {
            teamService.inviteTeam(managerId, requestDto);
        });

        verify(memberRepository).findByNicknameAndMemberCode(member.getNickname(),
            member.getMemberCode());
        verify(memberRepository).findById(manager.getMemberId());
        verify(teamInvitationRepository).findByMemberMemberIdAndTeamTeamIdAndAcceptStatus(
            memberId, team.getTeamId(), InvitationAcceptStatus.WAITING);
        verify(teamInvitationRepository, never()).save(any(TeamInvitation.class));

        // after
        member.setTeam(null);
    }

    @DisplayName("초대한 사용자가 대학 인증이 안되어 있는 경우 UnAuthorizedUnivException이 발생한다.")
    @Test
    void inviteTeam_UnAuthorizedUnivException() {
        // given
        InviteTeamRequestDto requestDto = MemberFixture.KAI.inviteTeamRequestDto();
        manager.setTeam(team);
        when(memberRepository.findByNicknameAndMemberCode(member.getNickname(),
            member.getMemberCode())).thenReturn(
            Optional.of(member));
        when(teamInvitationRepository.findByMemberMemberIdAndTeamTeamIdAndAcceptStatus(
            memberId, team.getTeamId(), InvitationAcceptStatus.WAITING)).thenReturn(
            Optional.empty());
        when(memberRepository.findById(manager.getMemberId())).thenReturn(Optional.of(manager));
        member.getCollegeInfo().saveMail(null);

        // when & then
        assertThrows(UnAuthorizedUnivException.class, () -> {
            teamService.inviteTeam(managerId, requestDto);
        });

        verify(memberRepository).findByNicknameAndMemberCode(member.getNickname(),
            member.getMemberCode());
        verify(memberRepository).findById(manager.getMemberId());
        verify(teamInvitationRepository).findByMemberMemberIdAndTeamTeamIdAndAcceptStatus(
            memberId, team.getTeamId(), InvitationAcceptStatus.WAITING);
        verify(teamInvitationRepository, never()).save(any(TeamInvitation.class));

        // after
        member.getCollegeInfo().saveMail("pppp1234@anyang.ac.kr");
    }

    @DisplayName("초대한 사용자의 성별이 팀장과 다른 경우 GenderNotMatchException이 발생한다.")
    @Test
    void inviteTeam_GenderNotMatchException() {
        // given
        InviteTeamRequestDto requestDto = MemberFixture.RIM.inviteTeamRequestDto();
        Member otherMember = MemberFixture.RIM.create();
        manager.setTeam(team);
        when(memberRepository.findByNicknameAndMemberCode(otherMember.getNickname(),
            otherMember.getMemberCode())).thenReturn(
            Optional.of(otherMember));
        when(memberRepository.findById(manager.getMemberId())).thenReturn(Optional.of(manager));

        // when & then
        assertThrows(GenderNotMatchException.class, () -> {
            teamService.inviteTeam(managerId, requestDto);
        });

        verify(memberRepository).findByNicknameAndMemberCode(otherMember.getNickname(),
            otherMember.getMemberCode());
        verify(memberRepository).findById(manager.getMemberId());
        verify(teamInvitationRepository, never()).findByMemberMemberIdAndTeamTeamIdAndAcceptStatus(
            otherMember.getMemberId(), team.getTeamId(), InvitationAcceptStatus.WAITING);
        verify(teamInvitationRepository, never()).save(any(TeamInvitation.class));
    }

    @DisplayName("이미 초대를 보낸 사용자인 경우 InvitationAlreadyExistsException이 발생한다.")
    @Test
    void inviteTeam_InvitationAlreadyExistsException() {
        // given
        InviteTeamRequestDto requestDto = MemberFixture.KAI.inviteTeamRequestDto();
        manager.setTeam(team);
        when(memberRepository.findByNicknameAndMemberCode(member.getNickname(),
            member.getMemberCode())).thenReturn(
            Optional.of(member));
        when(memberRepository.findById(manager.getMemberId())).thenReturn(Optional.of(manager));
        when(teamInvitationRepository.findByMemberMemberIdAndTeamTeamIdAndAcceptStatus(
            memberId, team.getTeamId(), InvitationAcceptStatus.WAITING)).thenReturn(
            Optional.of(TeamInvitation.builder()
                .member(member)
                .team(team)
                .build()));

        // when & then
        assertThrows(InvitationAlreadyExistsException.class, () -> {
            teamService.inviteTeam(managerId, requestDto);
        });

        verify(memberRepository).findByNicknameAndMemberCode(member.getNickname(),
            member.getMemberCode());
        verify(memberRepository).findById(manager.getMemberId());
        verify(teamInvitationRepository).findByMemberMemberIdAndTeamTeamIdAndAcceptStatus(
            memberId, team.getTeamId(), InvitationAcceptStatus.WAITING);
        verify(teamInvitationRepository, never()).save(any(TeamInvitation.class));
    }

    @DisplayName("팀 초대 신청 수락에 성공한다.")
    @Test
    void invitationAccept_Success() {
        // given
        when(memberRepository.findById(inviteMember.getMemberId())).thenReturn(
            Optional.of(inviteMember));
        when(teamInvitationRepository.findByTeamInvitationIdAndMemberMemberId(
            invitation.getTeamInvitationId(), inviteMember.getMemberId())).thenReturn(
            Optional.of(invitation));

        // when
        teamService.takeAcceptStatus(inviteMember.getMemberId(), invitation.getTeamInvitationId(),
            true);

        // then
        verify(memberRepository).findById(inviteMember.getMemberId());
        verify(teamInvitationRepository).findByTeamInvitationIdAndMemberMemberId(
            invitation.getTeamInvitationId(), inviteMember.getMemberId());
        assertEquals(InvitationAcceptStatus.ACCEPT, invitation.getAcceptStatus());
        assertEquals(team, inviteMember.getTeam());

        // after
        inviteMember.setTeam(null);
        invitation.updateAcceptStatus(InvitationAcceptStatus.WAITING);
    }

    @DisplayName("팀 초대 신청 거절에 성공한다.")
    @Test
    void invitationReject_Success() {
        // given
        when(memberRepository.findById(inviteMember.getMemberId())).thenReturn(
            Optional.of(inviteMember));
        when(teamInvitationRepository.findByTeamInvitationIdAndMemberMemberId(
            invitation.getTeamInvitationId(), inviteMember.getMemberId())).thenReturn(
            Optional.of(invitation));

        // when
        teamService.takeAcceptStatus(inviteMember.getMemberId(), invitation.getTeamInvitationId(),
            false);

        // then
        verify(memberRepository).findById(inviteMember.getMemberId());
        verify(teamInvitationRepository).findByTeamInvitationIdAndMemberMemberId(
            invitation.getTeamInvitationId(), inviteMember.getMemberId());
        assertEquals(InvitationAcceptStatus.REJECT, invitation.getAcceptStatus());

        // after
        invitation.updateAcceptStatus(InvitationAcceptStatus.WAITING);
    }

    @DisplayName("팀 초대가 존재하지 않는 경우 InvitationNotFoundException이 발생한다.")
    @Test
    void takeAcceptStatus_InvitationNotFoundException() {
        // given
        when(memberRepository.findById(inviteMember.getMemberId())).thenReturn(
            Optional.of(inviteMember));
        when(teamInvitationRepository.findByTeamInvitationIdAndMemberMemberId(
            invitation.getTeamInvitationId(), inviteMember.getMemberId())).thenReturn(
            Optional.empty());

        // when & then
        Long inviteMemberId = inviteMember.getMemberId();
        Long invitationId = invitation.getTeamInvitationId();
        assertThrows(InvitationNotFoundException.class, () -> {
            teamService.takeAcceptStatus(inviteMemberId, invitationId, true);
        });

        verify(memberRepository).findById(inviteMember.getMemberId());
        verify(teamInvitationRepository).findByTeamInvitationIdAndMemberMemberId(
            invitation.getTeamInvitationId(), inviteMember.getMemberId());
    }

    @DisplayName("이미 활성화된 팀일 경우 초대 신청을 수락 하면 TeamAlreadyActiveException이 발생한다..")
    @Test
    void takeAcceptStatus_TeamAlreadyActiveException() {
        // given
        team.setActive(true);
        when(memberRepository.findById(inviteMember.getMemberId())).thenReturn(
            Optional.of(inviteMember));
        when(teamInvitationRepository.findByTeamInvitationIdAndMemberMemberId(
            invitation.getTeamInvitationId(), inviteMember.getMemberId())).thenReturn(
            Optional.of(invitation));

        // when & then
        Long inviteMemberId = inviteMember.getMemberId();
        Long invitationId = invitation.getTeamInvitationId();
        assertThrows(TeamAlreadyActiveException.class, () -> {
            teamService.takeAcceptStatus(inviteMemberId, invitationId, true);
        });

        verify(memberRepository).findById(inviteMember.getMemberId());
        verify(teamInvitationRepository).findByTeamInvitationIdAndMemberMemberId(
            invitation.getTeamInvitationId(), inviteMember.getMemberId());
        assertEquals(InvitationAcceptStatus.WAITING, invitation.getAcceptStatus());

        // after
        team.setActive(false);
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
        assertThrows(NotBelongToTeamException.class, () -> {
            teamService.getTeamMemberList(managerId);
        });

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
        teamService.deleteTeam(managerId, response);

        // then
        verify(memberRepository).findById(managerId);
        verify(teamRepository).delete(team);
        verify(tokenInjector).injectToken(any(HttpServletResponse.class),
            any(MemberPrincipal.class));
        assertEquals(Role.USER, manager.getRole());

        // after
        manager.setRole(Role.MANAGER);
    }

    @DisplayName("팀이 없는 경우 팀 삭제를 요청하면 NotBelongToTeamException이 발생한다.")
    @Test
    void deleteTeam_NotBelongToTeamException() {
        // given
        manager.setTeam(null);
        when(memberRepository.findById(managerId)).thenReturn(
            Optional.of(manager));

        // when & then
        assertThrows(NotBelongToTeamException.class, () -> {
            teamService.deleteTeam(managerId, response);
        });

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
        member.setTeam(team);

        // when
        teamService.deleteTeamMember(manager.getMemberId(), memberId);

        // then
        verify(memberRepository).findById(managerId);
        verify(memberRepository).findById(memberId);
        assertNull(member.getTeam());
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
        assertThrows(NotBelongToTeamException.class, () -> {
            teamService.deleteTeamMember(managerId, memberId);
        });

        // then
        verify(memberRepository).findById(managerId);
        verify(memberRepository).findById(managerId);
        assertNull(manager.getTeam());

        // after
        manager.setTeam(team);
    }

    @DisplayName("팀장 자신을 팀에서 삭제하려 하면 ManagerSelfDeletionException이 발생한다.")
    @Test
    void deleteTeamMember_ManagerSelfDeletionException() {
        // given & when & then
        assertThrows(ManagerSelfDeletionException.class, () -> {
            teamService.deleteTeamMember(managerId, managerId);
        });

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
        assertThrows(NonTeamMemberException.class, () -> {
            teamService.deleteTeamMember(managerId, memberId);
        });

        verify(memberRepository).findById(managerId);
        verify(memberRepository).findById(memberId);
    }
}
