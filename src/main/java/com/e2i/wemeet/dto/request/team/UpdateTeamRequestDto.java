package com.e2i.wemeet.dto.request.team;

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
public record UpdateTeamRequestDto(

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
    @Length(min = 10, max = 150)
    String introduction,

    @NotNull
    String chatLink,

    @NotNull
    @Size(min = 1, max = 3)
    @Valid
    List<TeamMemberRequestDto> members
) {

}
