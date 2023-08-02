package com.e2i.wemeet.domain.meeting;

import com.e2i.wemeet.domain.base.BaseTimeEntity;
import com.e2i.wemeet.domain.meeting.data.AcceptStatus;
import com.e2i.wemeet.domain.team.Team;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
public class MeetingRequest extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long meetingRequestId;

    @ManyToOne
    @JoinColumn(name = "teamId", referencedColumnName = "teamId", nullable = false)
    private Team team;

    @ManyToOne
    @JoinColumn(name = "partnerTeamId", referencedColumnName = "teamId", nullable = false)
    private Team partnerTeam;

    // TODO :: converter
    // @Convert
    @Column(nullable = false)
    private AcceptStatus acceptStatus;

    @Column(length = 50)
    private String message;

    public MeetingRequest(Team team, Team partnerTeam, AcceptStatus acceptStatus, String message) {
        this.team = team;
        this.partnerTeam = partnerTeam;
        this.acceptStatus = acceptStatus;
        this.message = message;
    }
}
