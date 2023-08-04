package com.e2i.wemeet.domain.team_member;

import com.e2i.wemeet.domain.base.BaseTimeEntity;
import com.e2i.wemeet.domain.base.converter.MbtiConverter;
import com.e2i.wemeet.domain.member.data.CollegeInfo;
import com.e2i.wemeet.domain.member.data.Mbti;
import com.e2i.wemeet.domain.team.Team;
import jakarta.persistence.Convert;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
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
public class TeamMember extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long teamMemberId;

    @Embedded
    private CollegeInfo collegeInfo;

    @Convert(converter = MbtiConverter.class)
    private Mbti mbti;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "teamId")
    private Team team;

    @Builder
    public TeamMember(CollegeInfo collegeInfo, Mbti mbti, Team team) {
        this.collegeInfo = collegeInfo;
        this.mbti = mbti;
        this.team = team;
    }

    public void setTeam(Team team) {
        this.team = team;
        if (!team.getTeamMembers().contains(this)) {
            team.getTeamMembers().add(this);
        }
    }
}

