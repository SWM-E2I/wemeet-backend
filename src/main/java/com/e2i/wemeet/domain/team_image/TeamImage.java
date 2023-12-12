package com.e2i.wemeet.domain.team_image;

import com.e2i.wemeet.domain.base.CreateTimeEntity;
import com.e2i.wemeet.domain.team.Team;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
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
@Table(name = "TEAM_IMAGE")
@Entity
public class TeamImage extends CreateTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long teamImageId;

    @Column(length = 150, nullable = false)
    private String teamImageUrl;

    @Column(nullable = false)
    private Integer sequence;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "teamId")
    private Team team;

    @Builder
    public TeamImage(String teamImageUrl, Integer sequence, Team team) {
        this.teamImageUrl = teamImageUrl;
        this.sequence = sequence;
        this.team = team;
    }
}

