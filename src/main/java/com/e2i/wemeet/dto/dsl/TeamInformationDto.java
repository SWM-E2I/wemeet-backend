package com.e2i.wemeet.dto.dsl;

import com.e2i.wemeet.domain.team.Team;
import com.e2i.wemeet.domain.team.data.AdditionalActivity;
import com.e2i.wemeet.domain.team.data.DrinkRate;
import com.e2i.wemeet.domain.team.data.DrinkWithGame;
import com.e2i.wemeet.domain.team.data.Region;
import com.e2i.wemeet.domain.team_member.TeamMember;
import com.e2i.wemeet.dto.response.team.TeamMemberResponseDto;
import java.time.LocalDateTime;
import java.util.List;
import lombok.Builder;
import lombok.Getter;

@Getter
public class TeamInformationDto {

    private final Long teamId;
    private List<TeamMemberResponseDto> teamMembers;
    private final Integer memberNum;
    private final Region region;
    private final DrinkRate drinkRate;
    private final DrinkWithGame drinkWithGame;
    private final AdditionalActivity additionalActivity;
    private final String introduction;
    private final Boolean isDeleted;

    @Builder
    public TeamInformationDto(Long teamId, Integer memberNum, Region region, DrinkRate drinkRate,
        DrinkWithGame drinkWithGame, AdditionalActivity additionalActivity, String introduction, LocalDateTime deletedAt) {
        this.teamId = teamId;
        this.memberNum = memberNum;
        this.region = region;
        this.drinkRate = drinkRate;
        this.drinkWithGame = drinkWithGame;
        this.additionalActivity = additionalActivity;
        this.introduction = introduction;
        this.isDeleted = deletedAt != null;
    }

    public static TeamInformationDto of(Team team) {
        TeamInformationDto dto = TeamInformationDto.builder()
            .teamId(team.getTeamId())
            .memberNum(team.getMemberNum())
            .region(team.getRegion())
            .drinkRate(team.getDrinkRate())
            .drinkWithGame(team.getDrinkWithGame())
            .additionalActivity(team.getAdditionalActivity())
            .introduction(team.getIntroduction())
            .deletedAt(team.getDeletedAt())
            .build();

        dto.setTeamMember(team.getTeamMembers());
        return dto;
    }

    public void setTeamMembers(List<TeamMemberInformationDto> teamMembers) {
        this.teamMembers = teamMembers.stream()
            .map(TeamMemberInformationDto::toTeamMemberResponseDto)
            .toList();
    }

    private void setTeamMember(List<TeamMember> teamMembers) {
        this.teamMembers = teamMembers.stream()
            .map(TeamMemberResponseDto::of)
            .toList();
    }
}
