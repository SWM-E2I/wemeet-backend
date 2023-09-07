package com.e2i.wemeet.dto.dsl;

import com.e2i.wemeet.domain.member.data.CollegeType;
import com.e2i.wemeet.domain.member.data.Mbti;
import com.e2i.wemeet.domain.team.data.Region;
import com.querydsl.core.annotations.QueryProjection;
import java.time.LocalDateTime;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class MeetingInformationDto {

    private Long meetingId;
    private LocalDateTime meetingAcceptTime;
    private boolean isOver;
    private LocalDateTime deletedAt;
    private Long teamId;
    private int memberCount;
    private Region region;

    private Long partnerLeaderId;
    private String partnerLeaderNickname;
    private Mbti partnerLeaderMbti;
    private String partnerLeaderLowProfileUrl;
    private String partnerLeaderCollegeName;
    private CollegeType partnerLeaderCollegeType;
    private String partnerLeaderAdmissionYear;
    private Boolean partnerLeaderImageAuth;

    @QueryProjection
    public MeetingInformationDto(Long meetingId, LocalDateTime meetingAcceptTime, boolean isOver, LocalDateTime deletedAt, Long teamId,
        int memberCount,
        Region region, Long partnerLeaderId, String partnerLeaderNickname, Mbti partnerLeaderMbti, String partnerLeaderLowProfileUrl,
        String partnerLeaderCollegeName, CollegeType partnerLeaderCollegeType, String partnerLeaderAdmissionYear, Boolean partnerLeaderImageAuth) {
        this.meetingId = meetingId;
        this.meetingAcceptTime = meetingAcceptTime;
        this.isOver = isOver;
        this.deletedAt = deletedAt;
        this.teamId = teamId;
        this.memberCount = memberCount;
        this.region = region;
        this.partnerLeaderId = partnerLeaderId;
        this.partnerLeaderNickname = partnerLeaderNickname;
        this.partnerLeaderMbti = partnerLeaderMbti;
        this.partnerLeaderLowProfileUrl = partnerLeaderLowProfileUrl;
        this.partnerLeaderCollegeName = partnerLeaderCollegeName;
        this.partnerLeaderCollegeType = partnerLeaderCollegeType;
        this.partnerLeaderAdmissionYear = partnerLeaderAdmissionYear;
        this.partnerLeaderImageAuth = partnerLeaderImageAuth;
    }

    public boolean isOver() {
        return isOver;
    }

    @Override
    public String toString() {
        return "MeetingInformationDto{" +
            "meetingId=" + meetingId +
            ", meetingAcceptTime=" + meetingAcceptTime +
            ", isOver=" + isOver +
            ", deletedAt=" + deletedAt +
            ", teamId=" + teamId +
            ", memberCount=" + memberCount +
            ", region=" + region +
            ", partnerLeaderId=" + partnerLeaderId +
            ", partnerLeaderNickname='" + partnerLeaderNickname + '\'' +
            ", partnerLeaderMbti=" + partnerLeaderMbti +
            ", partnerLeaderLowProfileUrl='" + partnerLeaderLowProfileUrl + '\'' +
            ", partnerLeaderCollegeName='" + partnerLeaderCollegeName + '\'' +
            '}';
    }
}
