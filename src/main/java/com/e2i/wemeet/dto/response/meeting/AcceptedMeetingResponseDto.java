package com.e2i.wemeet.dto.response.meeting;

import com.e2i.wemeet.domain.team.data.Region;
import com.e2i.wemeet.dto.dsl.MeetingInformationDto;
import java.time.LocalDateTime;
import java.util.List;
import lombok.Builder;
import lombok.Getter;

@Getter
public class AcceptedMeetingResponseDto {

    private final Long meetingId;
    private final Long teamId;
    private final int memberCount;
    private final Region region;
    private final boolean isDeleted;
    private final LocalDateTime meetingAcceptTime;
    private final List<String> teamProfileImageUrl;
    private final LeaderResponseDto leader;
    private boolean isExpired;

    @Builder
    private AcceptedMeetingResponseDto(Long meetingId, Long teamId, int memberCount, Region region, boolean isDeleted,
        LocalDateTime meetingAcceptTime,
        List<String> teamProfileImageUrl, LeaderResponseDto leader, boolean isExpired) {
        this.meetingId = meetingId;
        this.teamId = teamId;
        this.memberCount = memberCount;
        this.region = region;
        this.isDeleted = isDeleted;
        this.meetingAcceptTime = meetingAcceptTime;
        this.teamProfileImageUrl = teamProfileImageUrl;
        this.leader = leader;
        this.isExpired = isExpired;
    }

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
            .isExpired(meetingInformationDto.isOver())
            .build();
    }

    public void expired() {
        this.isExpired = true;
    }
}
