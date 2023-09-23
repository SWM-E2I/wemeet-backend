package com.e2i.wemeet.dto.response.suggestion;

import java.util.List;
import lombok.Builder;

@Builder
public record CheckSuggestionResponseDto(
    boolean isReceivedSuggestion,
    List<SuggestionHistoryTeamDto> teams
) {

}
