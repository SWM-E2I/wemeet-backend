package com.e2i.wemeet.dto.request.meeting;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

/*
 * 미팅 신청 요청 DTO
 * */
public record SendMeetingRequestDto(
    @NotNull
    @Positive
    Long partnerTeamId
) {

}
