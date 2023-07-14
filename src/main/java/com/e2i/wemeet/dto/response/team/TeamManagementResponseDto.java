package com.e2i.wemeet.dto.response.team;

import java.util.List;
import lombok.Builder;

@Builder
public record TeamManagementResponseDto(
    Long managerId,
    String teamCode,
    List<TeamMemberResponseDto> members
) {

}
