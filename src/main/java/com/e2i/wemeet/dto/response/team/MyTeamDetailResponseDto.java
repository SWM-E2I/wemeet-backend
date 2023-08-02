package com.e2i.wemeet.dto.response.team;

import com.e2i.wemeet.domain.team.data.AdditionalActivity;
import java.util.List;
import lombok.Builder;

@Builder
public record MyTeamDetailResponseDto(
    int memberCount,
    String region,
    String drinkingOption,
    List<String> preferenceMeetingTypeList,
    AdditionalActivity additionalActivity,
    String introduction,
    boolean managerImageAuth
) {

}
