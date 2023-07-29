package com.e2i.wemeet.service.team;

import com.e2i.wemeet.domain.member.Member;
import com.e2i.wemeet.domain.member.MemberRepository;
import com.e2i.wemeet.domain.team.Team;
import com.e2i.wemeet.domain.team.invitation.InvitationAcceptStatus;
import com.e2i.wemeet.domain.team.invitation.TeamInvitation;
import com.e2i.wemeet.domain.team.invitation.TeamInvitationRepository;
import com.e2i.wemeet.dto.request.team.InviteTeamRequestDto;
import com.e2i.wemeet.exception.badrequest.GenderNotMatchException;
import com.e2i.wemeet.exception.badrequest.InvitationAlreadyExistsException;
import com.e2i.wemeet.exception.badrequest.InvitationAlreadySetException;
import com.e2i.wemeet.exception.notfound.InvitationNotFoundException;
import com.e2i.wemeet.exception.notfound.MemberNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
public class TeamInvitationServiceImpl implements TeamInvitationService {

    private final MemberRepository memberRepository;
    private final TeamInvitationRepository teamInvitationRepository;

    @Transactional
    @Override
    public void inviteTeam(Long memberId, InviteTeamRequestDto inviteTeamRequestDto) {
        Member teamMember = memberRepository.findByNicknameAndMemberCode(
                inviteTeamRequestDto.nickname(),
                inviteTeamRequestDto.memberCode())
            .orElseThrow(MemberNotFoundException::new);

        Member manager = findMember(memberId);
        Team team = manager.getTeam();

        validateInvitation(teamMember, manager, team);

        teamInvitationRepository.save(
            TeamInvitation.builder()
                .acceptStatus(InvitationAcceptStatus.WAITING)
                .member(teamMember)
                .team(team)
                .build());
    }

    @Transactional
    @Override
    public void takeAcceptStatus(Long memberId, Long invitationId, boolean accepted) {
        TeamInvitation teamInvitation =
            teamInvitationRepository.findByTeamInvitationIdAndMemberMemberId(
                    invitationId, memberId)
                .orElseThrow(InvitationNotFoundException::new);

        if (teamInvitation.getAcceptStatus() != InvitationAcceptStatus.WAITING) {
            throw new InvitationAlreadySetException();
        }

        if (accepted) {
            acceptInvitation(teamInvitation);
        } else {
            rejectInvitation(teamInvitation);
        }
    }

    /*
     * 초대 신청이 가능한지 확인
     */
    private void validateInvitation(Member teamMember, Member manager, Team team) {
        // 초대한 사용자와 성별이 다른 경우
        if (manager.getGender() != teamMember.getGender()) {
            throw new GenderNotMatchException();
        }

        // 이미 초대한 사용자인 경우
        if (isInvitationExist(teamMember, team)) {
            throw new InvitationAlreadyExistsException();
        }
    }

    private boolean isInvitationExist(Member member, Team team) {
        return teamInvitationRepository.findByMemberMemberIdAndTeamTeamIdAndAcceptStatus(
                member.getMemberId(),
                team.getTeamId(), InvitationAcceptStatus.WAITING)
            .isPresent();
    }

    private void acceptInvitation(TeamInvitation teamInvitation) {
        Team team = teamInvitation.getTeam();

        teamInvitation.updateAcceptStatus(InvitationAcceptStatus.ACCEPT);
        team.addMember(teamInvitation.getMember());

        if (team.getMembers().size() == team.getMemberCount()) {
            team.activateTeam();
        }
    }

    private void rejectInvitation(TeamInvitation teamInvitation) {
        teamInvitation.updateAcceptStatus(InvitationAcceptStatus.REJECT);
    }

    private Member findMember(Long memberId) {
        return memberRepository.findById(memberId)
            .orElseThrow(MemberNotFoundException::new);
    }
}
