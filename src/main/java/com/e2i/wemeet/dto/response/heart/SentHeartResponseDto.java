package com.e2i.wemeet.dto.response.heart;

import com.e2i.wemeet.dto.dsl.HeartTeamData;
import com.e2i.wemeet.dto.response.suggestion.TeamLeaderResponseDto;
import java.time.LocalDateTime;
import java.util.List;
import lombok.Builder;

@Builder
public record SentHeartResponseDto(
    Long teamId,
    int memberNum,
    String region,
    String profileImageURL,
    String mainImageURL,
    LocalDateTime sentTime,
    TeamLeaderResponseDto leader
) {

    public static SentHeartResponseDto of(HeartTeamData data) {
        return SentHeartResponseDto.builder()
            .teamId(data.teamId())
            .memberNum(data.memberNum())
            .region(data.region().getName())
            .profileImageURL(data.teamLeader().profileImageUrl())
            .mainImageURL(data.teamMainImageUrl())
            .sentTime(data.createdAt())
            .leader(TeamLeaderResponseDto.of(data.teamLeader())
            ).build();
    }

    public static List<SentHeartResponseDto> of(List<HeartTeamData> data) {
        if (data == null || data.isEmpty()) {
            return List.of();
        }

        return data.stream().map(SentHeartResponseDto::of).toList();
    }
}
