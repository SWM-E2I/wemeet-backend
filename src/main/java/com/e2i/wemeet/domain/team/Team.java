package com.e2i.wemeet.domain.team;

import com.e2i.wemeet.domain.base.BaseTimeEntity;
import com.e2i.wemeet.domain.base.converter.AdditionalActivityConverter;
import com.e2i.wemeet.domain.base.converter.DrinkRateConverter;
import com.e2i.wemeet.domain.base.converter.DrinkWithGameConverter;
import com.e2i.wemeet.domain.base.converter.GenderConverter;
import com.e2i.wemeet.domain.base.converter.RegionConverter;
import com.e2i.wemeet.domain.member.Member;
import com.e2i.wemeet.domain.member.data.Gender;
import com.e2i.wemeet.domain.member.data.Role;
import com.e2i.wemeet.domain.team.data.AdditionalActivity;
import com.e2i.wemeet.domain.team.data.DrinkRate;
import com.e2i.wemeet.domain.team.data.DrinkWithGame;
import com.e2i.wemeet.domain.team.data.Region;
import com.e2i.wemeet.domain.team_member.TeamMember;
import com.e2i.wemeet.dto.request.team.UpdateTeamRequestDto;
import com.e2i.wemeet.exception.badrequest.TeamHasBeenDeletedException;
import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.Entity;
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

    @Convert(converter = GenderConverter.class)
    private Gender gender;

    @Convert(converter = RegionConverter.class)
    @Column(length = 20, nullable = false)
    private Region region;

    @Convert(converter = DrinkRateConverter.class)
    @Column(nullable = false)
    private DrinkRate drinkRate;

    @Convert(converter = DrinkWithGameConverter.class)
    @Column(nullable = false)
    private DrinkWithGame drinkWithGame;

    @Convert(converter = AdditionalActivityConverter.class)
    private AdditionalActivity additionalActivity;

    @Column(length = 150, nullable = false)
    private String introduction;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "team_leader_id", updatable = false, nullable = false)
    private Member teamLeader;

    @OneToMany(mappedBy = "team")
    private List<TeamMember> teamMembers = new ArrayList<>();

    private LocalDateTime deletedAt;

    @Builder
    public Team(Integer memberNum, Region region, DrinkRate drinkRate,
        DrinkWithGame drinkWithGame, AdditionalActivity additionalActivity,
        String introduction, Member teamLeader) {
        setTeamLeader(teamLeader);
        this.memberNum = memberNum;
        this.region = region;
        this.drinkRate = drinkRate;
        this.drinkWithGame = drinkWithGame;
        this.additionalActivity = additionalActivity;
        this.introduction = introduction;
    }

    /*
     * 팀장 설정은 생성자에서만 가능
     * 연관 관계 설정은 Team 에서 진행
     */
    private void setTeamLeader(final Member teamLeader) {
        this.teamLeader = teamLeader;
        this.gender = teamLeader.getGender();
        teamLeader.setTeam(this);
        teamLeader.setRole(Role.MANAGER);
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


    public void update(UpdateTeamRequestDto updateTeamRequestDto) {
        this.memberNum = updateTeamRequestDto.members().size() + 1;
        this.region = Region.valueOf(updateTeamRequestDto.region());
        this.drinkRate = DrinkRate.valueOf(updateTeamRequestDto.drinkRate());
        this.drinkWithGame = DrinkWithGame.valueOf(updateTeamRequestDto.drinkWithGame());
        this.additionalActivity = AdditionalActivity.valueOf(
            updateTeamRequestDto.additionalActivity());
        this.introduction = updateTeamRequestDto.introduction();
    }
}

