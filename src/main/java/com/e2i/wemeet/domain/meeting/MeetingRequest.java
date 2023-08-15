package com.e2i.wemeet.domain.meeting;

import static com.e2i.wemeet.domain.meeting.data.AcceptStatus.PENDING;

import com.e2i.wemeet.domain.base.BaseTimeEntity;
import com.e2i.wemeet.domain.base.converter.AcceptStatusConverter;
import com.e2i.wemeet.domain.meeting.data.AcceptStatus;
import com.e2i.wemeet.domain.team.Team;
import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.AccessLevel;
import lombok.Builder;
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

    @Convert(converter = AcceptStatusConverter.class)
    @Column(nullable = false)
    private AcceptStatus acceptStatus;

    @Column(length = 50)
    private String message;

    @Builder
    public MeetingRequest(Team team, Team partnerTeam, String message) {
        this.acceptStatus = PENDING;
        this.team = team;
        this.partnerTeam = partnerTeam;
        this.message = message;
    }

    public void changeStatus(AcceptStatus status) {
        this.acceptStatus = status;
    }
}
