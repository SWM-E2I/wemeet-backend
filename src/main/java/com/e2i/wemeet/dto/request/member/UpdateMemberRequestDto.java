package com.e2i.wemeet.dto.request.member;

import com.e2i.wemeet.util.validator.bean.MbtiValid;
import com.e2i.wemeet.util.validator.bean.NicknameValid;
import lombok.Builder;

@Builder
public record UpdateMemberRequestDto(
    @NicknameValid
    String nickname,

    @MbtiValid
    String mbti

) {

}
