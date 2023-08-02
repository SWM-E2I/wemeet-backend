package com.e2i.wemeet.dto.response.member;

import com.e2i.wemeet.domain.member.data.Gender;
import com.e2i.wemeet.domain.member.data.Mbti;
import lombok.Builder;

// TODO :: service refactoring
@Builder
public record MemberDetailResponseDto(
    String nickname,
    Gender gender,
    Mbti mbti,
    String college,
    String collegeType,
    String admissionYear,
    String introduction
) {

}
