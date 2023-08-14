package com.e2i.wemeet.dto.response.team;

import com.e2i.wemeet.domain.team.Team;
import com.e2i.wemeet.domain.team.data.AdditionalActivity;
import com.e2i.wemeet.domain.team.data.TeamImageData;
import java.util.List;
import java.util.Optional;
import lombok.Builder;

@Builder
public record MyTeamDetailResponseDto(
    int memberNum,
    String region,
    String drinkRate,
    String drinkWithGame,
    String additionalActivity,
    String introduction,
    List<TeamImageDto> images,
    List<TeamMemberResponseDto> members
) {

    public static MyTeamDetailResponseDto of(Team team, List<TeamImageData> teamImages) {
        Optional<AdditionalActivity> additionalActivity = Optional.ofNullable(
            team.getAdditionalActivity());

        return MyTeamDetailResponseDto.builder()
            .memberNum(team.getMemberNum())
            .region(team.getRegion().getName())
            .drinkRate(team.getDrinkRate().getName())
            .drinkWithGame(team.getDrinkWithGame().getName())
            .additionalActivity(additionalActivity.map(AdditionalActivity::getName).orElse(null))
            .introduction(team.getIntroduction())
            .images(
                teamImages.stream()
                    .map(teamImage -> TeamImageDto.of(teamImage.url()))
                    .toList())
            .members(
                team.getTeamMembers().stream()
                    .map(TeamMemberResponseDto::of)
                    .toList())
            .build();
    }

}
