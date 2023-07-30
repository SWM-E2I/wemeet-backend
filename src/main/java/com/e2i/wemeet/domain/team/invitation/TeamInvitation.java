package com.e2i.wemeet.domain.team.invitation;

import static java.lang.Boolean.TRUE;

import com.e2i.wemeet.domain.base.BaseTimeEntity;
import com.e2i.wemeet.domain.member.Member;
import com.e2i.wemeet.domain.team.Team;
import com.e2i.wemeet.exception.badrequest.TeamAlreadyActiveException;
import com.e2i.wemeet.exception.badrequest.TeamAlreadyExistsException;
import com.e2i.wemeet.exception.unauthorized.UnAuthorizedUnivException;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "TEAM_INVITATION")
@Entity
public class TeamInvitation extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long teamInvitationId;

    @Column(nullable = false)
    @Enumerated(value = EnumType.STRING)
    private InvitationAcceptStatus acceptStatus;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "memberId")
    private Member member;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "teamId")
    private Team team;

    @Builder
    public TeamInvitation(
        Long teamInvitationId, InvitationAcceptStatus acceptStatus, Member member, Team team) {
        validateInvitationMember(member);
        validateInvitationTeam(team);

        this.teamInvitationId = teamInvitationId;
        this.acceptStatus = acceptStatus;
        this.member = member;
        this.team = team;
    }

    private void validateInvitationMember(final Member member) {
        isTeamExist(member);
        isUnivAuth(member);
    }

    private void validateInvitationTeam(final Team team) {
        isActiveTeam(team);
    }

    private void isTeamExist(Member member) {
        if (member.getTeam() != null) {
            throw new TeamAlreadyExistsException();
        }
    }

    private void isUnivAuth(Member member) {
        if (member.getCollegeInfo().getMail() == null) {
            throw new UnAuthorizedUnivException();
        }
    }

    private void isActiveTeam(Team team) {
        if (TRUE.equals(team.getIsActive()) && team.getMembers().size() == team.getMemberCount()) {
            throw new TeamAlreadyActiveException();
        }
    }

    public void updateAcceptStatus(InvitationAcceptStatus acceptStatus) {
        validateInvitationMember(this.member);
        validateInvitationTeam(this.team);

        this.acceptStatus = acceptStatus;
    }
}
