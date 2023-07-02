package com.e2i.wemeet.dto.response.member;

import lombok.Builder;

@Builder
public record MemberInfoResponseDto(
    String nickname,
    String memberCode,
    String profileImage,
    boolean univAuth,
    boolean imageAuth
) {

}
