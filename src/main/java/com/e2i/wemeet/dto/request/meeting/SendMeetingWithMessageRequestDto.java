package com.e2i.wemeet.dto.request.meeting;

import com.e2i.wemeet.exception.ErrorCode;
import com.e2i.wemeet.exception.badrequest.InvalidDataFormatException;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import org.hibernate.validator.constraints.Length;
import org.springframework.util.StringUtils;

public record SendMeetingWithMessageRequestDto(
    @NotNull
    @Positive
    Long partnerTeamId,

    @Length(max = 50)
    String message

) {

    public String getMessageWithValid() {
        if (!StringUtils.hasText(message) | message.length() > 50) {
            throw new InvalidDataFormatException(ErrorCode.INVALID_MESSAGE_FORMAT);
        }
        return this.message;
    }
}
