package com.e2i.wemeet.dto.request.meeting;

import com.e2i.wemeet.util.validator.bean.AcceptStatusValid;
import com.e2i.wemeet.util.validator.bean.KakaoOpenChatLinkValid;
import jakarta.validation.constraints.NotNull;

public record MeetingHandleRequestDto(

    @NotNull
    @AcceptStatusValid
    String status,

    @KakaoOpenChatLinkValid
    String kakaoOpenChatLink

) {

}
