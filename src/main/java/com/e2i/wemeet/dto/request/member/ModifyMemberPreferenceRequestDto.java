package com.e2i.wemeet.dto.request.member;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.util.List;
import lombok.Builder;

@Builder
public record ModifyMemberPreferenceRequestDto(
    @NotBlank(message = "{not.blank.drinking.option}")
    String drinkingOption,
    @NotBlank(message = "{not.blank.start.preference.admission.year}")
    String startPreferenceAdmissionYear,
    @NotBlank(message = "{not.blank.end.preference.admission.year}")
    String endPreferenceAdmissionYear,
    @NotBlank(message = "{not.blank.same.college.state}")
    String sameCollegeState,
    @NotNull(message = "{not.null.is.avoid.friends}")
    boolean isAvoidedFriends,
    @NotBlank(message = "{not.blank.preference.mbti}")
    String preferenceMbti,
    @NotNull(message = "{not.null.preference.meeting.type}")
    List<String> preferenceMeetingTypeList
) {

}
