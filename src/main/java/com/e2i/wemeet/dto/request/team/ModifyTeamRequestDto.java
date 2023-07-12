package com.e2i.wemeet.dto.request.team;

import jakarta.annotation.Nullable;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.util.List;
import lombok.Builder;

@Builder
public record ModifyTeamRequestDto(
    @NotBlank(message = "{not.blank.region}")
    String region,

    @NotBlank(message = "{not.blank.drinking.option}")
    String drinkingOption,

    @Size(min = 1, max = 5, message = "{size.validate.preference.meeting.type}")
    @NotNull(message = "{not.null.preference.meeting.type}")
    List<String> preferenceMeetingTypeList,

    @Nullable
    String additionalActivity,

    @NotBlank(message = "{not.blank.introduction}")
    String introduction) {

}
