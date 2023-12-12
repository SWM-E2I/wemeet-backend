package com.e2i.wemeet.dto.request.team;

import jakarta.validation.constraints.NotEmpty;
import java.util.List;

public record DeleteTeamImageRequestDto(

    @NotEmpty
    List<String> deleteImageUrls

) {

}
