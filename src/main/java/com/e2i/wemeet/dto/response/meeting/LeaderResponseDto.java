package com.e2i.wemeet.dto.response.meeting;

import com.e2i.wemeet.domain.member.Member;
import com.e2i.wemeet.domain.member.data.Mbti;
import com.e2i.wemeet.dto.dsl.MeetingInformationDto;
import com.e2i.wemeet.dto.dsl.MeetingRequestInformationDto;

public record LeaderResponseDto(
    Long leaderId,
    String nickname,
    Mbti mbti,
    String collegeName,
    String leaderLowProfileImageUrl

) {

    public static LeaderResponseDto of(final Member leader, final String collegeName) {
        return new LeaderResponseDto(
            leader.getMemberId(),
            leader.getNickname(),
            leader.getMbti(),
            collegeName,
            leader.getProfileImage().getLowUrl()
        );
    }

    public static LeaderResponseDto of(final MeetingInformationDto meetingInformationDto) {
        return new LeaderResponseDto(
            meetingInformationDto.getPartnerLeaderId(),
            meetingInformationDto.getPartnerLeaderNickname(),
            meetingInformationDto.getPartnerLeaderMbti(),
            meetingInformationDto.getPartnerLeaderCollegeName(),
            meetingInformationDto.getPartnerLeaderLowProfileUrl()
        );
    }

    public static LeaderResponseDto of(final MeetingRequestInformationDto meetingRequestInformationDto) {
        return new LeaderResponseDto(
            meetingRequestInformationDto.getPartnerLeaderId(),
            meetingRequestInformationDto.getPartnerLeaderNickname(),
            meetingRequestInformationDto.getPartnerLeaderMbti(),
            meetingRequestInformationDto.getPartnerLeaderCollegeName(),
            meetingRequestInformationDto.getPartnerLeaderLowProfileUrl()
        );
    }

}
