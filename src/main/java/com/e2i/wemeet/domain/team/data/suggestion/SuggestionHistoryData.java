package com.e2i.wemeet.domain.team.data.suggestion;

import com.e2i.wemeet.domain.team.data.Region;
import lombok.Builder;

@Builder
public record SuggestionHistoryData(
    Long teamId,
    int memberNum,
    Region region,
    String teamMainImageUrl,
    boolean isLiked,
    TeamLeaderData teamLeader
) {

}
