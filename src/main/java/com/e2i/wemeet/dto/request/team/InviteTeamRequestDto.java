package com.e2i.wemeet.dto.request.team;

import jakarta.validation.constraints.NotBlank;
import lombok.Builder;

@Builder
public record InviteTeamRequestDto(
    @NotBlank(message = "{not.blank.nickname}")
    String nickname,
    @NotBlank(message = "{not.blank.member.code}")
    String memberCode
) {

}
