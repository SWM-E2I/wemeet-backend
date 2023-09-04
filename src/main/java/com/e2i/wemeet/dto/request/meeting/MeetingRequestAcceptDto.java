package com.e2i.wemeet.dto.request.meeting;

import com.e2i.wemeet.util.validator.bean.KakaoOpenChatLinkValid;

public record MeetingRequestAcceptDto(

    @KakaoOpenChatLinkValid
    String kakaoOpenChatLink

) {

}
