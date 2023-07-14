package com.e2i.wemeet.domain.team.invitation;

import com.e2i.wemeet.domain.base.BaseTimeEntity;
import com.e2i.wemeet.domain.member.Member;
import com.e2i.wemeet.domain.team.Team;
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
    private InvitationAcceptStatus acceptStatus = InvitationAcceptStatus.WAITING;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "memberId")
    private Member member;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "teamId")
    private Team team;

    @Builder
    public TeamInvitation(
        Long teamInvitationId, InvitationAcceptStatus acceptStatus, Member member, Team team) {
        this.teamInvitationId = teamInvitationId;
        this.acceptStatus = acceptStatus;
        this.member = member;
        this.team = team;
    }

    public void updateAcceptStatus(InvitationAcceptStatus acceptStatus) {
        this.acceptStatus = acceptStatus;
    }
}
