package com.e2i.wemeet.dto.response.meeting;

import com.e2i.wemeet.domain.team.data.Region;
import java.time.LocalDateTime;
import java.util.List;

public record AcceptedMeetingResponseDto(

    Long meetingId,
    Long teamId,
    int memberCount,
    Region region,
    boolean isDeleted,
    LocalDateTime meetingRequestTime,
    LocalDateTime meetingAcceptTime,
    List<String> teamProfileImageUrl,
    LeaderResponse leader

) {

}
