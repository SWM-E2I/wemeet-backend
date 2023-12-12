package com.e2i.wemeet.domain.meeting;

import com.e2i.wemeet.domain.base.BaseTimeEntity;
import com.e2i.wemeet.domain.team.Team;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
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
@Table(name = "MEETING")
@Entity
public class Meeting extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long meetingId;

    @ManyToOne
    @JoinColumn(name = "teamId", referencedColumnName = "teamId", nullable = false)
    private Team team;

    @ManyToOne
    @JoinColumn(name = "partnerTeamId", referencedColumnName = "teamId", nullable = false)
    private Team partnerTeam;

    @Column(nullable = false)
    private Boolean isOver;

    @Builder
    public Meeting(Team team, Team partnerTeam) {
        this.team = team;
        this.partnerTeam = partnerTeam;
        this.isOver = false;
    }

    public void over() {
        this.isOver = true;
    }

}
