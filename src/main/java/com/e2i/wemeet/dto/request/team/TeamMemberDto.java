package com.e2i.wemeet.dto.request.team;

import jakarta.validation.constraints.NotBlank;

public record TeamMemberDto(
    @NotBlank(message = "{not.null.team.member.college}")
    String college,
    @NotBlank(message = "{not.null.team.member.college.type}")
    String collegeType,
    @NotBlank(message = "{not.null.team.member.admmision.year}")
    String addmissionYear,
    @NotBlank(message = "{not.null.team.member.mbti}")
    String mbti
) {

}
