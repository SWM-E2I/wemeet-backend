package com.e2i.wemeet.dto.response.team;

import java.util.List;
import lombok.Builder;

@Builder
public record TeamManagementResponseDto(
    String teamCode,
    List<TeamMemberResponseDto> members
) {

}
