package com.e2i.wemeet.dto.response.suggestion;

import com.e2i.wemeet.domain.team.data.suggestion.SuggestionHistoryData;
import java.util.List;
import lombok.Builder;

@Builder
public record SuggestionHistoryTeamDto(
    Long teamId,
    boolean isLiked,
    int memberNum,
    String region,
    String profileImageURL,
    String mainImageURL,
    TeamLeaderResponseDto leader
) {

    public static SuggestionHistoryTeamDto of(SuggestionHistoryData data) {
        return SuggestionHistoryTeamDto.builder()
            .teamId(data.teamId())
            .isLiked(data.isLiked())
            .memberNum(data.memberNum())
            .region(data.region().getName())
            .profileImageURL(data.teamLeader().profileImageUrl())
            .mainImageURL(data.teamMainImageUrl())
            .leader(TeamLeaderResponseDto.of(data.teamLeader())
            ).build();
    }

    public static List<SuggestionHistoryTeamDto> of(List<SuggestionHistoryData> data) {
        return data.stream()
            .map(SuggestionHistoryTeamDto::of)
            .toList();
    }
}
