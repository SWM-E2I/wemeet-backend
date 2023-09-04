package com.e2i.wemeet.dto.response.meeting;

import com.e2i.wemeet.domain.meeting.data.AcceptStatus;
import com.e2i.wemeet.domain.team.data.Region;
import com.e2i.wemeet.dto.dsl.MeetingRequestInformationDto;
import com.e2i.wemeet.dto.response.LeaderResponseDto;
import java.time.LocalDateTime;
import java.util.List;
import lombok.Builder;
import lombok.Getter;

@Getter
public class SentMeetingResponseDto extends MeetingRequestResponseDto {

    private final Long teamId;
    private final int memberCount;
    private final Region region;
    private final String message;
    private final List<String> teamProfileImageUrl;
    private final LeaderResponseDto leader;

    @Builder
    private SentMeetingResponseDto(Long meetingRequestId, AcceptStatus acceptStatus, LocalDateTime requestTime, boolean partnerTeamDeleted,
        Long teamId, int memberCount, Region region, String message, List<String> teamProfileImageUrl, LeaderResponseDto leader) {
        super(meetingRequestId, acceptStatus, requestTime, partnerTeamDeleted);
        this.teamId = teamId;
        this.memberCount = memberCount;
        this.region = region;
        this.message = message;
        this.teamProfileImageUrl = teamProfileImageUrl;
        this.leader = leader;
    }

    public static SentMeetingResponseDto of(final MeetingRequestInformationDto requestDto,
        final List<String> teamProfileImageUrl) {
        return SentMeetingResponseDto.builder()
            .meetingRequestId(requestDto.getMeetingRequestId())
            .teamId(requestDto.getTeamId())
            .memberCount(requestDto.getMemberCount())
            .region(requestDto.getRegion())
            .partnerTeamDeleted(requestDto.getDeletedAt() != null)
            .acceptStatus(requestDto.getAcceptStatus())
            .requestTime(requestDto.getRequestSentTime())
            .message(requestDto.getMessage())
            .teamProfileImageUrl(teamProfileImageUrl)
            .leader(LeaderResponseDto.of(requestDto))
            .build();
    }

    @Override
    public String toString() {
        return "SentMeetingResponseDto{" +
            "teamId=" + teamId +
            ", memberCount=" + memberCount +
            ", region=" + region +
            ", message='" + message + '\'' +
            ", teamProfileImageUrl=" + teamProfileImageUrl +
            ", leader=" + leader +
            ", meetingRequestId=" + meetingRequestId +
            ", acceptStatus=" + acceptStatus +
            ", requestTime=" + requestTime +
            ", partnerTeamDeleted=" + partnerTeamDeleted +
            '}';
    }
}
