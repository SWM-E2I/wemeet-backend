package com.e2i.wemeet.dto.dsl;

import com.e2i.wemeet.domain.team.data.Region;
import com.e2i.wemeet.domain.team.data.suggestion.TeamLeaderData;
import java.time.LocalDateTime;
import lombok.Builder;

@Builder
public record HeartTeamData(
    Long teamId,
    int memberNum,
    Region region,
    String teamMainImageUrl,
    LocalDateTime createdAt,
    TeamLeaderData teamLeader) {

}
