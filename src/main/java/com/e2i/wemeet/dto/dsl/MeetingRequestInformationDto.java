package com.e2i.wemeet.dto.dsl;

import com.e2i.wemeet.domain.meeting.data.AcceptStatus;
import com.e2i.wemeet.domain.member.data.Mbti;
import com.e2i.wemeet.domain.team.data.Region;
import com.querydsl.core.annotations.QueryProjection;
import java.time.LocalDateTime;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class MeetingRequestInformationDto {

    private Long meetingRequestId;
    private LocalDateTime requestSentTime;
    private String message;
    private AcceptStatus acceptStatus;
    private Long teamId;
    private int memberCount;
    private Region region;
    private LocalDateTime deletedAt;

    private Long partnerLeaderId;
    private String partnerLeaderNickname;
    private Mbti partnerLeaderMbti;
    private String partnerLeaderLowProfileUrl;
    private String partnerLeaderCollegeName;

    @QueryProjection
    public MeetingRequestInformationDto(Long meetingRequestId, LocalDateTime requestSentTime, String message, AcceptStatus acceptStatus, Long teamId,
        int memberCount, Region region, LocalDateTime deletedAt, Long partnerLeaderId, String partnerLeaderNickname, Mbti partnerLeaderMbti,
        String partnerLeaderLowProfileUrl, String partnerLeaderCollegeName) {
        this.meetingRequestId = meetingRequestId;
        this.requestSentTime = requestSentTime;
        this.message = message;
        this.acceptStatus = acceptStatus;
        this.teamId = teamId;
        this.memberCount = memberCount;
        this.region = region;
        this.deletedAt = deletedAt;
        this.partnerLeaderId = partnerLeaderId;
        this.partnerLeaderNickname = partnerLeaderNickname;
        this.partnerLeaderMbti = partnerLeaderMbti;
        this.partnerLeaderLowProfileUrl = partnerLeaderLowProfileUrl;
        this.partnerLeaderCollegeName = partnerLeaderCollegeName;
    }

    @Override
    public String toString() {
        return "MeetingRequestInformationDto{" +
            "meetingRequestId=" + meetingRequestId +
            ", teamId=" + teamId +
            ", memberCount=" + memberCount +
            ", region=" + region +
            ", deletedAt=" + deletedAt +
            ", requestSentTime=" + requestSentTime +
            ", partnerLeaderId=" + partnerLeaderId +
            ", partnerLeaderNickname='" + partnerLeaderNickname + '\'' +
            ", partnerLeaderMbti=" + partnerLeaderMbti +
            ", partnerLeaderLowProfileUrl='" + partnerLeaderLowProfileUrl + '\'' +
            ", partnerLeaderCollegeName='" + partnerLeaderCollegeName + '\'' +
            '}';
    }

}
