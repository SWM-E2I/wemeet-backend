package com.e2i.wemeet.dto.response.team;

import lombok.Builder;

@Builder
public record TeamMemberResponseDto(
    Long memberId,
    String nickname,
    String memberCode,
    String profileImage,
    boolean isAccepted
) {

}
