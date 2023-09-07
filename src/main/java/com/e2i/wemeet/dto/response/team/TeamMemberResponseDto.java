package com.e2i.wemeet.dto.response.team;

import com.e2i.wemeet.domain.member.data.Mbti;
import com.e2i.wemeet.domain.team_member.TeamMember;
import lombok.Builder;

@Builder
public record TeamMemberResponseDto(
    String college,
    String collegeType,
    String admissionYear,
    Mbti mbti
) {

    public static TeamMemberResponseDto of(TeamMember teamMember) {
        return TeamMemberResponseDto.builder()
            .college(teamMember.getCollegeName())
            .collegeType(teamMember.getCollegeInfo().getCollegeType().getDescription())
            .admissionYear(teamMember.getCollegeInfo().getAdmissionYear())
            .mbti(teamMember.getMbti())
            .build();
    }
}
