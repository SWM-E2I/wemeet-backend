package com.e2i.wemeet.dto.request.member;

public record CollegeInfoRequestDto(
    String college,
    String collegeType,
    int admissionYear,
    String mail
) {

}
