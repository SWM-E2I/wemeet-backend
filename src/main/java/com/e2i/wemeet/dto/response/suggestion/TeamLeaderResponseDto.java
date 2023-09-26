package com.e2i.wemeet.dto.response.suggestion;

import com.e2i.wemeet.domain.team.data.suggestion.TeamLeaderData;
import lombok.Builder;

@Builder
public record TeamLeaderResponseDto(
    String nickname,
    String mbti,
    String college,
    String admissionYear
) {

    public static TeamLeaderResponseDto of(TeamLeaderData data) {
        return TeamLeaderResponseDto.builder()
            .nickname(data.nickname())
            .mbti(data.mbti().name())
            .college(data.college())
            .admissionYear(data.admissionYear())
            .build();
    }
}
