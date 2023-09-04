package com.e2i.wemeet.dto.response.team;

import lombok.Builder;

@Builder
public record MyTeamResponseDto(
    boolean hasTeam,
    MyTeamDetailResponseDto team
) {

    public static MyTeamResponseDto of(boolean hasTeam) {
        return MyTeamResponseDto.builder()
            .hasTeam(hasTeam)
            .build();
    }

    public static MyTeamResponseDto of(boolean hasTeam, MyTeamDetailResponseDto team) {
        return MyTeamResponseDto.builder()
            .hasTeam(hasTeam)
            .team(team)
            .build();
    }
}
