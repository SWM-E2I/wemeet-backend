package com.e2i.wemeet.dto.response.team;

import lombok.Builder;

@Builder
public record MyTeamResponseDto(
    boolean hasTeam,
    MyTeamDetailResponseDto data
) {

    public static MyTeamResponseDto of(boolean hasTeam) {
        return MyTeamResponseDto.builder()
            .hasTeam(hasTeam)
            .build();
    }

    public static MyTeamResponseDto of(boolean hasTeam, MyTeamDetailResponseDto data) {
        return MyTeamResponseDto.builder()
            .hasTeam(hasTeam)
            .data(data)
            .build();
    }
}
