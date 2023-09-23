package com.e2i.wemeet.dto.request.meeting;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import org.hibernate.validator.constraints.Length;

public record SendMeetingWithMessageRequestDto(

    @NotNull
    @Positive
    Long partnerTeamId,

    @NotBlank
    @Length(max = 50)
    String message

) {

}
