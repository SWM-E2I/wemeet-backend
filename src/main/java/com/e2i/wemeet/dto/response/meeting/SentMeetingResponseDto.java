package com.e2i.wemeet.dto.response.meeting;

import com.e2i.wemeet.domain.team.data.Region;
import java.time.LocalDateTime;

public record SentMeetingResponseDto(

    Long meetingRequestId,
    Long teamId,
    int memberCount,
    Region region,
    boolean isDeleted,
    LocalDateTime meetingRequestTime,
    LeaderResponse leader

) {

}
