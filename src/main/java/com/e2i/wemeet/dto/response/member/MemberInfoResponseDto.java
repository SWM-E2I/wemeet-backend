package com.e2i.wemeet.dto.response.member;

import lombok.Builder;

// TODO :: service refactoring
@Builder
public record MemberInfoResponseDto(
    String nickname,
    String memberCode,
    String profileImage,
    boolean univAuth,
    boolean imageAuth
) {

}
