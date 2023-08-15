package com.e2i.wemeet.domain.team.data.suggestion;

import com.e2i.wemeet.domain.team.Team;

public record SuggestionTeamData(
    Team team,
    String teamMainImageUrl,
    TeamLeaderData teamLeader
) {

}
