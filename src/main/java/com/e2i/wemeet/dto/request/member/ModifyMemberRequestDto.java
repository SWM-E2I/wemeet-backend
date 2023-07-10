package com.e2i.wemeet.dto.request.member;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.util.List;
import lombok.Builder;

@Builder
public record ModifyMemberRequestDto(
    @NotBlank(message = "{not.blank.nickname}")
    String nickname,
    @NotNull(message = "{not.null.mbti}")
    String mbti,
    @NotBlank(message = "{not.null.introduction}")
    String introduction,

    @NotNull(message = "{not.null.member.interest}")
    List<String> memberInterestList
) {

}
