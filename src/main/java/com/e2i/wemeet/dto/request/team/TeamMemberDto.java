package com.e2i.wemeet.dto.request.team;

import com.e2i.wemeet.domain.code.Code;
import com.e2i.wemeet.domain.member.data.Mbti;
import com.e2i.wemeet.domain.team.Team;
import com.e2i.wemeet.domain.team_member.TeamMember;
import com.e2i.wemeet.dto.request.member.CollegeInfoRequestDto;
import com.e2i.wemeet.util.validator.bean.MbtiValid;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;

@Builder
public record TeamMemberDto(

    @NotNull
    @Valid
    CollegeInfoRequestDto collegeInfo,
    
    @NotNull
    @MbtiValid
    String mbti
) {

    public TeamMember toEntity(Code collegeCode, Team team) {
        return TeamMember.builder()
            .collegeInfo(this.collegeInfo.toCollegeInfo(collegeCode))
            .mbti(Mbti.valueOf(this.mbti))
            .team(team)
            .build();
    }
}
