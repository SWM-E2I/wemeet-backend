package com.e2i.wemeet.dto.response.team;

import com.e2i.wemeet.domain.meeting.data.AcceptStatus;
import com.e2i.wemeet.domain.team.data.AdditionalActivity;
import com.e2i.wemeet.domain.team.data.DrinkRate;
import com.e2i.wemeet.domain.team.data.DrinkWithGame;
import com.e2i.wemeet.domain.team.data.Region;
import com.e2i.wemeet.dto.dsl.TeamInformationDto;
import com.e2i.wemeet.dto.response.LeaderResponseDto;
import java.util.List;

public record TeamDetailResponseDto(
    Long teamId,
    Boolean isDeleted,
    Boolean isLiked,
    AcceptStatus meetingRequestStatus,
    Boolean memberHasTeam,
    Integer memberNum,
    Region region,
    DrinkRate drinkRate,
    DrinkWithGame drinkWithGame,
    AdditionalActivity additionalActivity,
    String introduction,
    String chatLink,
    List<String> teamImageUrls,
    List<TeamMemberResponseDto> teamMembers,
    LeaderResponseDto leader
) {

    public static TeamDetailResponseDto of(final TeamInformationDto teamInformationDto,
        final LeaderResponseDto leader,
        final List<String> teamImageUrls) {
        return new TeamDetailResponseDto(
            teamInformationDto.getTeamId(),
            teamInformationDto.getIsDeleted(),
            teamInformationDto.getIsLiked(),
            teamInformationDto.getMeetingRequestStatus(),
            teamInformationDto.getMemberHasTeam(),
            teamInformationDto.getMemberNum(),
            teamInformationDto.getRegion(),
            teamInformationDto.getDrinkRate(),
            teamInformationDto.getDrinkWithGame(),
            teamInformationDto.getAdditionalActivity(),
            teamInformationDto.getIntroduction(),
            teamInformationDto.getChatLink(),
            teamImageUrls,
            teamInformationDto.getTeamMembers(),
            leader
        );
    }

}
