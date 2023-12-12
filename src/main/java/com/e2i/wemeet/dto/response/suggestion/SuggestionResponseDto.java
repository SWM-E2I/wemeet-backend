package com.e2i.wemeet.dto.response.suggestion;

import com.e2i.wemeet.domain.team.data.suggestion.SuggestionTeamData;
import java.util.List;
import lombok.Builder;

@Builder
public record SuggestionResponseDto(
    Long teamId,
    int memberNum,
    String region,
    String profileImageURL,
    String mainImageURL,
    TeamLeaderResponseDto leader
) {

    public static SuggestionResponseDto of(SuggestionTeamData data) {
        return SuggestionResponseDto.builder()
            .teamId(data.team().getTeamId())
            .memberNum(data.team().getMemberNum())
            .region(data.team().getRegion().getName())
            .profileImageURL(data.teamLeader().profileImageUrl())
            .mainImageURL(data.teamMainImageUrl())
            .leader(TeamLeaderResponseDto.builder()
                .college(data.teamLeader().college())
                .nickname(data.teamLeader().nickname())
                .mbti(data.teamLeader().mbti().name())
                .admissionYear(data.teamLeader().admissionYear())
                .emailAuthenticated(data.teamLeader().emailAuthenticated())
                .build()
            ).build();
    }

    public static List<SuggestionResponseDto> of(List<SuggestionTeamData> data) {
        return data.stream()
            .map(SuggestionResponseDto::of)
            .toList();
    }
}
