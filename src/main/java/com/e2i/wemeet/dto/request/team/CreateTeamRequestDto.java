package com.e2i.wemeet.dto.request.team;

import jakarta.annotation.Nullable;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.util.List;
import lombok.Builder;
import org.hibernate.validator.constraints.Length;

@Builder
public record CreateTeamRequestDto(
    @Max(value = 4, message = "{max.validate.member.num}")
    @Min(value = 2, message = "{min.validate.member.num}")
    int memberNum,

    @NotBlank(message = "{not.blank.region}")
    String region,

    @NotBlank(message = "{not.blank.drink.rate}")
    String drinkRate,

    @NotBlank(message = "{not.blank.drink.with.game}")
    String drinkWithGame,

    @Nullable
    String additionalActivity,

    @Length(min = 1, max = 150, message = "{length.validate.introduction}")
    @NotBlank(message = "{not.blank.introduction}")
    String introduction,

    @Size(min = 1, max = 3, message = "{size.validate.team.members}")
    @NotNull(message = "{not.null.team.members")
    List<TeamMemberDto> members
) {

}
