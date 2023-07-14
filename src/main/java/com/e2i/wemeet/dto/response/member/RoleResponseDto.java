package com.e2i.wemeet.dto.response.member;

import lombok.Builder;

@Builder
public record RoleResponseDto(
    boolean isManager,
    boolean hasTeam
) {

}
