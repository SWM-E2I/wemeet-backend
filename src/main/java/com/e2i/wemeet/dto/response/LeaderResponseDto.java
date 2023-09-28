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
    Boolean imageAuth,
    Boolean emailAuthenticated

) {

    public static LeaderResponseDto of(final Member leader) {
        return new LeaderResponseDto(
            leader.getMemberId(),
            leader.getNickname(),
            leader.getMbti(),
            leader.getCollegeInfo().getCollegeCode().getCodeValue(),
            leader.getCollegeInfo().getCollegeType(),
            leader.getCollegeInfo().getAdmissionYear(),
            leader.getProfileImage().getLowUrl(),
            leader.getProfileImage().getImageAuth(),
            (leader.getEmail() != null)
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
            meetingInformationDto.getPartnerLeaderImageAuth(),
            meetingInformationDto.getEmailAuthenticated()
        );
    }

    public static LeaderResponseDto of(
        final MeetingRequestInformationDto meetingRequestInformationDto) {
        return new LeaderResponseDto(
            meetingRequestInformationDto.getPartnerLeaderId(),
            meetingRequestInformationDto.getPartnerLeaderNickname(),
            meetingRequestInformationDto.getPartnerLeaderMbti(),
            meetingRequestInformationDto.getPartnerLeaderCollegeName(),
            meetingRequestInformationDto.getPartnerLeaderCollegeType(),
            meetingRequestInformationDto.getPartnerLeaderAdmissionYear(),
            meetingRequestInformationDto.getPartnerLeaderLowProfileUrl(),
            meetingRequestInformationDto.getPartnerLeaderImageAuth(),
            meetingRequestInformationDto.getEmailAuthenticated()
        );
    }

    @Override
    public String toString() {
        return "LeaderResponseDto{" +
            "leaderId=" + leaderId +
            ", nickname='" + nickname + '\'' +
            ", mbti=" + mbti +
            ", collegeName='" + collegeName + '\'' +
            ", collegeType=" + collegeType +
            ", admissionYear='" + admissionYear + '\'' +
            ", leaderLowProfileImageUrl='" + leaderLowProfileImageUrl + '\'' +
            ", imageAuth=" + imageAuth +
            ", emailAuthentication=" + emailAuthenticated +
            '}';
    }
}
