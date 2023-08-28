package com.e2i.wemeet.dto.response.meeting;

import static com.e2i.wemeet.domain.meeting.data.AcceptStatus.PENDING;

import com.e2i.wemeet.domain.meeting.data.AcceptStatus;
import java.time.LocalDateTime;
import lombok.Getter;

@Getter
public abstract class MeetingRequestResponseDto {

    protected Long meetingRequestId;
    protected AcceptStatus acceptStatus;
    protected LocalDateTime requestTime;
    protected boolean partnerTeamDeleted;

    protected MeetingRequestResponseDto(Long meetingRequestId, AcceptStatus acceptStatus, LocalDateTime requestTime, boolean partnerTeamDeleted) {
        this.meetingRequestId = meetingRequestId;
        this.acceptStatus = acceptStatus;
        this.requestTime = requestTime;
        this.partnerTeamDeleted = partnerTeamDeleted;
    }

    public boolean isPending() {
        return acceptStatus == PENDING;
    }

    public boolean isDeleted() {
        return partnerTeamDeleted;
    }

}
