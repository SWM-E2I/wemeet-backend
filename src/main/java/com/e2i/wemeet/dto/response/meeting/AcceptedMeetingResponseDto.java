package com.e2i.wemeet.dto.response.meeting;

import com.e2i.wemeet.domain.team.data.Region;
import com.e2i.wemeet.dto.dsl.MeetingInformationDto;
import java.time.LocalDateTime;
import java.util.List;
import lombok.Builder;

@Builder
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
        return AcceptedMeetingResponseDto.builder()
            .meetingId(meetingInformationDto.getMeetingId())
            .teamId(meetingInformationDto.getTeamId())
            .memberCount(meetingInformationDto.getMemberCount())
            .region(meetingInformationDto.getRegion())
            .isDeleted(meetingInformationDto.getDeletedAt() != null)
            .meetingAcceptTime(meetingInformationDto.getMeetingAcceptTime())
            .teamProfileImageUrl(teamProfileImageUrl)
            .leader(LeaderResponseDto.of(meetingInformationDto))
            .build();
    }

}
