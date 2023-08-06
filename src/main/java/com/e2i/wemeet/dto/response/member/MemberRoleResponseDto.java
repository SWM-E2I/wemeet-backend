package com.e2i.wemeet.dto.response.member;

import lombok.Builder;

@Builder
public record MemberRoleResponseDto(
    boolean isManager,
    boolean hasTeam
) {

}
