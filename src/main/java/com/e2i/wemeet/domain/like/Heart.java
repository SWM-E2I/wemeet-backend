package com.e2i.wemeet.domain.like;

import com.e2i.wemeet.domain.base.CreateTimeEntity;
import com.e2i.wemeet.domain.team.Team;
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
public class Heart extends CreateTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long heartId;

    @ManyToOne
    @JoinColumn(name = "teamId", referencedColumnName = "teamId", nullable = false)
    private Team team;

    @ManyToOne
    @JoinColumn(name = "partnerTeamId", referencedColumnName = "teamId", nullable = false)
    private Team partnerTeam;

    @Builder
    public Heart(Team team, Team partnerTeam) {
        this.team = team;
        this.partnerTeam = partnerTeam;
    }
}
