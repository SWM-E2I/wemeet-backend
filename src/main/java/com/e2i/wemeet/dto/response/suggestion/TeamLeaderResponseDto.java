package com.e2i.wemeet.dto.response.suggestion;

import com.e2i.wemeet.domain.member.data.Mbti;
import com.e2i.wemeet.domain.team.data.suggestion.TeamLeaderData;
import lombok.Builder;

@Builder
public record TeamLeaderResponseDto(
    String nickname,
    Mbti mbti,
    String college
) {

    public static TeamLeaderResponseDto of(TeamLeaderData data) {
        return TeamLeaderResponseDto.builder()
            .nickname(data.nickname())
            .mbti(data.mbti())
            .college(data.college())
            .build();
    }
}
