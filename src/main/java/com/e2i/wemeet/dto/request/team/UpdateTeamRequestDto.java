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
import org.springframework.web.multipart.MultipartFile;

@Builder
public record UpdateTeamRequestDto(
    @NotBlank(message = "{not.blank.region}")
    String region,

    @Max(value = 100, message = "{max.validate.drink.rate}")
    @Min(value = 0, message = "{min.validate.drink.rate}")
    int drinkRate,

    @NotBlank(message = "{not.blank.drink.with.game}")
    String drinkWithGame,

    @Nullable
    String additionalActivity,

    @Length(min = 1, max = 150, message = "{length.validate.introduction}")
    @NotBlank(message = "{not.blank.introduction}")
    String introduction,

    @Size(min = 1, max = 5, message = "{size.validate.team.images}")
    @NotNull(message = "{not.null.team.images")
    List<MultipartFile> images,

    @Size(min = 1, max = 3, message = "{size.validate.team.members}")
    @NotNull(message = "{not.null.team.members")
    List<TeamMemberDto> members
) {

}
