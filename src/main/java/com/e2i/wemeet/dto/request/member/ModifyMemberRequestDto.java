package com.e2i.wemeet.dto.request.member;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;

@Builder
public record ModifyMemberRequestDto(
    @NotBlank(message = "{not.blank.nickname}")
    String nickname,
    @NotNull(message = "{not.null.mbti}")
    String mbti,
    @NotBlank(message = "{not.null.introduction}")
    String introduction
) {

}
