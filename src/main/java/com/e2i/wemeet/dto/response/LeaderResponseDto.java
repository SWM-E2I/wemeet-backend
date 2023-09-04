package com.e2i.wemeet.dto.response;

import com.e2i.wemeet.domain.member.Member;
import com.e2i.wemeet.domain.member.data.CollegeType;
import com.e2i.wemeet.domain.member.data.Mbti;
import com.e2i.wemeet.dto.dsl.MeetingInformationDto;
import com.e2i.wemeet.dto.dsl.MeetingRequestInformationDto;

public record LeaderResponseDto(
    Long leaderId,
    String nickname,
    Mbti mbti,
    String collegeName,
    CollegeType collegeType,
    String admissionYear,
    String leaderLowProfileImageUrl,
    Boolean imageAuth

) {

    public static LeaderResponseDto of(final Member leader, final String collegeName) {
        return new LeaderResponseDto(
            leader.getMemberId(),
            leader.getNickname(),
            leader.getMbti(),
            collegeName,
            leader.getCollegeInfo().getCollegeType(),
            leader.getCollegeInfo().getAdmissionYear(),
            leader.getProfileImage().getLowUrl(),
            leader.getProfileImage().getImageAuth()
        );
    }

    public static LeaderResponseDto of(final MeetingInformationDto meetingInformationDto) {
        return new LeaderResponseDto(
            meetingInformationDto.getPartnerLeaderId(),
            meetingInformationDto.getPartnerLeaderNickname(),
            meetingInformationDto.getPartnerLeaderMbti(),
            meetingInformationDto.getPartnerLeaderCollegeName(),
            meetingInformationDto.getPartnerLeaderCollegeType(),
            meetingInformationDto.getPartnerLeaderAdmissionYear(),
            meetingInformationDto.getPartnerLeaderLowProfileUrl(),
            meetingInformationDto.getPartnerLeaderImageAuth()
        );
    }

    public static LeaderResponseDto of(final MeetingRequestInformationDto meetingRequestInformationDto) {
        return new LeaderResponseDto(
            meetingRequestInformationDto.getPartnerLeaderId(),
            meetingRequestInformationDto.getPartnerLeaderNickname(),
            meetingRequestInformationDto.getPartnerLeaderMbti(),
            meetingRequestInformationDto.getPartnerLeaderCollegeName(),
            meetingRequestInformationDto.getPartnerLeaderCollegeType(),
            meetingRequestInformationDto.getPartnerLeaderAdmissionYear(),
            meetingRequestInformationDto.getPartnerLeaderLowProfileUrl(),
            meetingRequestInformationDto.getPartnerLeaderImageAuth()
        );
    }

}
