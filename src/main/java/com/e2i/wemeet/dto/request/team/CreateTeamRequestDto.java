package com.e2i.wemeet.dto.request.team;

import com.e2i.wemeet.domain.member.Member;
import com.e2i.wemeet.domain.team.AdditionalActivity;
import com.e2i.wemeet.domain.team.Team;
import jakarta.annotation.Nullable;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.util.List;
import lombok.Builder;

@Builder
public record CreateTeamRequestDto(
    @Max(value = 4, message = "{max.validate.member.count}")
    @Min(value = 1, message = "{min.validate.member.count}")
    int memberCount,

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

    public Team toTeamEntity(String teamCode, Member member) {
        return Team.builder()
            .teamCode(teamCode)
            .memberCount(memberCount)
            .region(region)
            .additionalActivity(AdditionalActivity.findBy(additionalActivity))
            .drinkingOption(drinkingOption)
            .introduction(introduction)
            .teamLeader(member)
            .build();
    }

}
