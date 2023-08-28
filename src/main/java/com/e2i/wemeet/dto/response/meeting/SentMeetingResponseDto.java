package com.e2i.wemeet.dto.response.meeting;

import com.e2i.wemeet.domain.meeting.data.AcceptStatus;
import com.e2i.wemeet.domain.team.data.Region;
import com.e2i.wemeet.dto.dsl.MeetingRequestInformationDto;
import java.time.LocalDateTime;
import java.util.List;
import lombok.Builder;

@Builder
public record SentMeetingResponseDto(

    Long meetingRequestId,
    Long teamId,
    int memberCount,
    Region region,
    boolean isDeleted,
    AcceptStatus acceptStatus,
    LocalDateTime requestSentTime,
    String message,
    List<String> teamProfileImageUrl,
    LeaderResponseDto leader

) {

    public static SentMeetingResponseDto of(final MeetingRequestInformationDto requestDto,
        final List<String> teamProfileImageUrl) {
        return SentMeetingResponseDto.builder()
            .meetingRequestId(requestDto.getMeetingRequestId())
            .teamId(requestDto.getTeamId())
            .memberCount(requestDto.getMemberCount())
            .region(requestDto.getRegion())
            .isDeleted(requestDto.getDeletedAt() != null)
            .acceptStatus(requestDto.getAcceptStatus())
            .requestSentTime(requestDto.getRequestSentTime())
            .message(requestDto.getMessage())
            .teamProfileImageUrl(teamProfileImageUrl)
            .leader(LeaderResponseDto.of(requestDto))
            .build();
    }

    @Override
    public String toString() {
        return "SentMeetingResponseDto{" +
            "meetingRequestId=" + meetingRequestId +
            ", teamId=" + teamId +
            ", memberCount=" + memberCount +
            ", region=" + region +
            ", isDeleted=" + isDeleted +
            ", requestSentTime=" + requestSentTime +
            ", teamProfileImageUrl=" + teamProfileImageUrl +
            ", leader=" + leader +
            '}';
    }
}
