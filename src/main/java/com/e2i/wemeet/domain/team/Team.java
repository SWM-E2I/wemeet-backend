package com.e2i.wemeet.domain.team;

import com.e2i.wemeet.domain.base.BaseTimeEntity;
import com.e2i.wemeet.domain.base.converter.AdditionalActivityConverter;
import com.e2i.wemeet.domain.base.converter.DrinkWithGameConverter;
import com.e2i.wemeet.domain.base.converter.RegionConverter;
import com.e2i.wemeet.domain.member.Member;
import com.e2i.wemeet.domain.member.data.Gender;
import com.e2i.wemeet.domain.team.data.AdditionalActivity;
import com.e2i.wemeet.domain.team.data.DrinkWithGame;
import com.e2i.wemeet.domain.team.data.Region;
import com.e2i.wemeet.domain.team_member.TeamMember;
import com.e2i.wemeet.exception.badrequest.TeamHasBeenDeletedException;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "TEAM")
@Entity
public class Team extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long teamId;

    @Column(nullable = false)
    private Integer memberNum;

    @Column(length = 6, nullable = false)
    @Enumerated(value = EnumType.STRING)
    private Gender gender;

    @Convert(converter = RegionConverter.class)
    @Column(length = 20, nullable = false)
    private Region region;

    @Column(nullable = false)
    private Integer drinkRate;

    @Convert(converter = DrinkWithGameConverter.class)
    @Column(nullable = false)
    private DrinkWithGame drinkWithGame;

    @Convert(converter = AdditionalActivityConverter.class)
    private AdditionalActivity additionalActivity;

    @Column(length = 150, nullable = false)
    private String introduction;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "memberId")
    private Member teamLeader;

    @OneToMany(mappedBy = "team", cascade = CascadeType.PERSIST)
    private List<TeamMember> teamMembers = new ArrayList<>();

    private LocalDateTime deletedAt;

    @Builder
    public Team(Integer memberNum, Gender gender, Region region, Integer drinkRate, DrinkWithGame drinkWithGame,
        AdditionalActivity additionalActivity,
        String introduction, Member teamLeader, LocalDateTime deletedAt) {
        this.memberNum = memberNum;
        this.gender = gender;
        this.region = region;
        this.drinkRate = drinkRate;
        this.drinkWithGame = drinkWithGame;
        this.additionalActivity = additionalActivity;
        this.introduction = introduction;
        this.teamLeader = teamLeader;
        this.deletedAt = deletedAt;
    }

    public Team checkTeamValid() {
        if (this.getDeletedAt() != null) {
            throw new TeamHasBeenDeletedException();
        }
        return this;
    }

    public void addTeamMembers(List<TeamMember> teamMembers) {
        teamMembers.forEach(this::addTeamMember);
    }

    public void addTeamMember(TeamMember teamMember) {
        teamMember.setTeam(this);
    }
}
