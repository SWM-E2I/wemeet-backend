package com.e2i.wemeet.dto.response.meeting;

import com.e2i.wemeet.domain.team.data.Region;
import com.e2i.wemeet.dto.dsl.MeetingInformationDto;
import java.time.LocalDateTime;
import java.util.List;

public record AcceptedMeetingResponseDto(

    Long meetingId,
    Long teamId,
    int memberCount,
    Region region,
    boolean isDeleted,
    LocalDateTime meetingAcceptTime,
    List<String> teamProfileImageUrl,
    LeaderResponseDto leader

) {

    public static AcceptedMeetingResponseDto of(final MeetingInformationDto meetingInformationDto,
        final List<String> teamProfileImageUrl) {
        return new AcceptedMeetingResponseDto(
            meetingInformationDto.getMeetingId(),
            meetingInformationDto.getTeamId(),
            meetingInformationDto.getMemberCount(),
            meetingInformationDto.getRegion(),
            meetingInformationDto.getDeletedAt() != null,
            meetingInformationDto.getMeetingAcceptTime(),
            teamProfileImageUrl,
            LeaderResponseDto.of(meetingInformationDto)
        );
    }

}
