package com.e2i.wemeet.dto.request.member;

import com.e2i.wemeet.util.validator.bean.MbtiValid;
import lombok.Builder;
import org.hibernate.validator.constraints.Length;

@Builder
public record UpdateMemberRequestDto(
    @Length(min = 2, max = 10, message = "{length.nickname}")
    String nickname,

    @MbtiValid
    String mbti

) {

}
