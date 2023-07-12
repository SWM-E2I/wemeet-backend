package com.e2i.wemeet.dto.request.member;

import jakarta.validation.constraints.NotBlank;
import lombok.Builder;

@Builder
public record CollegeInfoRequestDto(
    @NotBlank(message = "{not.blank.college}")
    String college,
    @NotBlank(message = "{not.blank.college.type}")
    String collegeType,
    @NotBlank(message = "{not.blank.admission.year}")
    String admissionYear
) {

}
