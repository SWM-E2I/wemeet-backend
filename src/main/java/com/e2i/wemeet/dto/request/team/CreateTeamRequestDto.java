package com.e2i.wemeet.dto.request.team;

import com.e2i.wemeet.domain.member.Member;
import com.e2i.wemeet.domain.team.Team;
import com.e2i.wemeet.domain.team.data.AdditionalActivity;
import com.e2i.wemeet.domain.team.data.DrinkRate;
import com.e2i.wemeet.domain.team.data.DrinkWithGame;
import com.e2i.wemeet.domain.team.data.Region;
import com.e2i.wemeet.util.validator.bean.AdditionalActivityValid;
import com.e2i.wemeet.util.validator.bean.DrinkRateValid;
import com.e2i.wemeet.util.validator.bean.DrinkWithGameValid;
import com.e2i.wemeet.util.validator.bean.RegionValid;
import jakarta.annotation.Nullable;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.util.List;
import lombok.Builder;
import org.hibernate.validator.constraints.Length;

@Builder
public record CreateTeamRequestDto(

    @NotNull
    @RegionValid
    String region,

    @NotNull
    @DrinkRateValid
    String drinkRate,

    @NotNull
    @DrinkWithGameValid
    String drinkWithGame,

    @Nullable
    @AdditionalActivityValid
    String additionalActivity,

    @NotNull
    @Length(max = 150)
    String introduction,

    @NotNull
    @Size(min = 1, max = 3)
    @Valid
    List<TeamMemberDto> members
) {

    public Team toEntity(Member teamLeader) {
        return Team.builder()
            .memberNum(members.size() + 1)
            .region(Region.valueOf(region))
            .drinkRate(DrinkRate.valueOf(drinkRate))
            .drinkWithGame(DrinkWithGame.valueOf(drinkWithGame))
            .additionalActivity(AdditionalActivity.findBy(additionalActivity))
            .introduction(introduction)
            .teamLeader(teamLeader)
            .build();
    }
}
