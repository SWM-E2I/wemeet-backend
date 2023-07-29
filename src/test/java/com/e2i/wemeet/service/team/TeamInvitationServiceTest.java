package com.e2i.wemeet.service.team;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.e2i.wemeet.domain.member.Member;
import com.e2i.wemeet.domain.member.MemberRepository;
import com.e2i.wemeet.domain.team.Team;
import com.e2i.wemeet.domain.team.invitation.InvitationAcceptStatus;
import com.e2i.wemeet.domain.team.invitation.TeamInvitation;
import com.e2i.wemeet.domain.team.invitation.TeamInvitationRepository;
import com.e2i.wemeet.dto.request.team.InviteTeamRequestDto;
import com.e2i.wemeet.exception.badrequest.GenderNotMatchException;
import com.e2i.wemeet.exception.badrequest.InvitationAlreadyExistsException;
import com.e2i.wemeet.exception.badrequest.TeamAlreadyActiveException;
import com.e2i.wemeet.exception.badrequest.TeamAlreadyExistsException;
import com.e2i.wemeet.exception.notfound.InvitationNotFoundException;
import com.e2i.wemeet.exception.notfound.MemberNotFoundException;
import com.e2i.wemeet.exception.unauthorized.UnAuthorizedUnivException;
import com.e2i.wemeet.support.fixture.MemberFixture;
import com.e2i.wemeet.support.fixture.TeamFixture;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class TeamInvitationServiceTest {

    @Mock
    private MemberRepository memberRepository;

    @Mock
    private TeamInvitationRepository teamInvitationRepository;

    @InjectMocks
    private TeamInvitationServiceImpl teamInvitationService;

    private static final Member member = MemberFixture.KAI.create();
    private static final Member manager = MemberFixture.SEYUN.create();
    private static final Member inviteMember = MemberFixture.JEONGYEOL.create();
    private static final Team team = TeamFixture.TEST_TEAM.create();

    private static final Long managerId = manager.getMemberId();
    private static final Long memberId = member.getMemberId();

    private static final TeamInvitation invitation = TeamInvitation.builder()
        .teamInvitationId(1L)
        .team(team)
        .member(inviteMember)
        .acceptStatus(InvitationAcceptStatus.WAITING)
        .build();

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
        teamInvitationService.inviteTeam(manager.getMemberId(), requestDto);

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
            teamInvitationService.inviteTeam(managerId, requestDto);
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
        team.addMember(manager);
        team.addMember(inviteMember);
        team.activateTeam();

        // when & then
        assertThrows(TeamAlreadyActiveException.class, () -> {
            teamInvitationService.inviteTeam(managerId, requestDto);
        });

        verify(memberRepository).findByNicknameAndMemberCode(member.getNickname(),
            member.getMemberCode());
        verify(memberRepository).findById(manager.getMemberId());
        verify(teamInvitationRepository).findByMemberMemberIdAndTeamTeamIdAndAcceptStatus(
            memberId, team.getTeamId(), InvitationAcceptStatus.WAITING);
        verify(teamInvitationRepository, never()).save(any(TeamInvitation.class));

        // after
        team.deleteMember(inviteMember);
        team.deactivateTeam();
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
            teamInvitationService.inviteTeam(managerId, requestDto);
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
            teamInvitationService.inviteTeam(managerId, requestDto);
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
            teamInvitationService.inviteTeam(managerId, requestDto);
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
            teamInvitationService.inviteTeam(managerId, requestDto);
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
        when(teamInvitationRepository.findByTeamInvitationIdAndMemberMemberId(
            invitation.getTeamInvitationId(), inviteMember.getMemberId())).thenReturn(
            Optional.of(invitation));

        // when
        teamInvitationService.takeAcceptStatus(inviteMember.getMemberId(),
            invitation.getTeamInvitationId(),
            true);

        // then
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
        when(teamInvitationRepository.findByTeamInvitationIdAndMemberMemberId(
            invitation.getTeamInvitationId(), inviteMember.getMemberId())).thenReturn(
            Optional.of(invitation));

        // when
        teamInvitationService.takeAcceptStatus(inviteMember.getMemberId(),
            invitation.getTeamInvitationId(),
            false);

        // then
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
        when(teamInvitationRepository.findByTeamInvitationIdAndMemberMemberId(
            invitation.getTeamInvitationId(), inviteMember.getMemberId())).thenReturn(
            Optional.empty());

        // when & then
        Long inviteMemberId = inviteMember.getMemberId();
        Long invitationId = invitation.getTeamInvitationId();
        assertThrows(InvitationNotFoundException.class, () -> {
            teamInvitationService.takeAcceptStatus(inviteMemberId, invitationId, true);
        });

        verify(teamInvitationRepository).findByTeamInvitationIdAndMemberMemberId(
            invitation.getTeamInvitationId(), inviteMember.getMemberId());
    }

    @DisplayName("이미 활성화된 팀일 경우 초대 신청을 수락 하면 TeamAlreadyActiveException이 발생한다..")
    @Test
    void takeAcceptStatus_TeamAlreadyActiveException() {
        // given
        team.addMember(manager);
        team.addMember(member);
        team.activateTeam();
        when(teamInvitationRepository.findByTeamInvitationIdAndMemberMemberId(
            invitation.getTeamInvitationId(), inviteMember.getMemberId())).thenReturn(
            Optional.of(invitation));

        // when & then
        Long inviteMemberId = inviteMember.getMemberId();
        Long invitationId = invitation.getTeamInvitationId();
        assertThrows(TeamAlreadyActiveException.class, () -> {
            teamInvitationService.takeAcceptStatus(inviteMemberId, invitationId, true);
        });

        verify(teamInvitationRepository).findByTeamInvitationIdAndMemberMemberId(
            invitation.getTeamInvitationId(), inviteMember.getMemberId());
        assertEquals(InvitationAcceptStatus.WAITING, invitation.getAcceptStatus());

        // after
        team.deleteMember(member);
        team.deactivateTeam();
    }
}
