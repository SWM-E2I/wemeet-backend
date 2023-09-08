package com.e2i.wemeet.dto.response.heart;

import com.e2i.wemeet.dto.dsl.HeartTeamData;
import com.e2i.wemeet.dto.response.suggestion.TeamLeaderResponseDto;
import java.time.LocalDateTime;
import java.util.List;
import lombok.Builder;

@Builder
public record ReceivedHeartResponseDto(
    Long teamId,
    int memberNum,
    String region,
    String profileImageURL,
    String mainImageURL,
    LocalDateTime receivedTime,
    TeamLeaderResponseDto leader
) {

    public static ReceivedHeartResponseDto of(HeartTeamData data) {
        return ReceivedHeartResponseDto.builder()
            .teamId(data.teamId())
            .memberNum(data.memberNum())
            .region(data.region().getName())
            .profileImageURL(data.teamLeader().profileImageUrl())
            .mainImageURL(data.teamMainImageUrl())
            .receivedTime(data.createdAt())
            .leader(TeamLeaderResponseDto.of(data.teamLeader())
            ).build();
    }

    public static List<ReceivedHeartResponseDto> of(List<HeartTeamData> data) {
        return data.stream().map(ReceivedHeartResponseDto::of).toList();
    }
}
