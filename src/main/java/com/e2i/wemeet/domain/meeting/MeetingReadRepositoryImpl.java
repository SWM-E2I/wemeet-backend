package com.e2i.wemeet.domain.meeting;

import static com.e2i.wemeet.domain.code.QCode.code;
import static com.e2i.wemeet.domain.meeting.QMeeting.meeting;
import static com.e2i.wemeet.domain.meeting.QMeetingRequest.meetingRequest;
import static com.e2i.wemeet.domain.member.QMember.member;
import static com.e2i.wemeet.domain.team.QTeam.team;
import static com.e2i.wemeet.domain.team_image.QTeamImage.teamImage;

import com.e2i.wemeet.domain.member.QMember;
import com.e2i.wemeet.domain.team.QTeam;
import com.e2i.wemeet.domain.team.Team;
import com.e2i.wemeet.dto.dsl.MeetingInformationDto;
import com.e2i.wemeet.dto.dsl.MeetingRequestInformationDto;
import com.e2i.wemeet.dto.dsl.QMeetingInformationDto;
import com.e2i.wemeet.dto.dsl.QMeetingRequestInformationDto;
import com.e2i.wemeet.dto.dsl.QTeamCheckProxyDto;
import com.e2i.wemeet.dto.dsl.TeamCheckProxyDto;
import com.e2i.wemeet.dto.response.meeting.AcceptedMeetingResponseDto;
import com.e2i.wemeet.dto.response.meeting.ReceivedMeetingResponseDto;
import com.e2i.wemeet.dto.response.meeting.SentMeetingResponseDto;
import com.e2i.wemeet.exception.notfound.TeamNotFoundException;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class MeetingReadRepositoryImpl implements MeetingReadRepository {

    private final JPAQueryFactory queryFactory;
    private final EntityManager entityManager;

    private final QMember partnerTeamLeader = new QMember("teamLeader");
    private final QTeam partnerTeam = new QTeam("partnerTeam");

    // LeaderId로 Team 프록시 객체 조회
    @Override
    public Team findTeamReferenceByLeaderId(final Long memberLeaderId) {
        Long teamId = findTeamIdByLeaderId(memberLeaderId);

        return entityManager.getReference(Team.class, teamId);
    }

    // LeaderId로 TeamId 조회
    @Override
    public Long findTeamIdByLeaderId(final Long memberLeaderId) {
        Optional<TeamCheckProxyDto> teamCheckProxy = findTeamCheckProxyByLeaderId(memberLeaderId);
        return getTeamIdWithCheckValid(teamCheckProxy);
    }

    // TeamId로 Team 프록시 객체 조회
    @Override
    public Team findTeamReferenceById(final Long teamId) {
        Optional<TeamCheckProxyDto> teamCheckProxy = findTeamCheckProxyByTeamId(teamId);
        getTeamIdWithCheckValid(teamCheckProxy);

        return entityManager.getReference(Team.class, teamId);
    }

    // 성사된 미팅 조회
    @Override
    public List<AcceptedMeetingResponseDto> findAcceptedMeetingList(final Long memberId) {
        List<MeetingInformationDto> meetingList = queryFactory.select(
                new QMeetingInformationDto(
                    meeting.meetingId,
                    meeting.createdAt.as("meetingAcceptTime"),
                    meeting.isOver,
                    partnerTeam.deletedAt,
                    partnerTeam.teamId,
                    partnerTeam.memberNum.as("memberCount"),
                    partnerTeam.region,
                    partnerTeamLeader.memberId.as("partnerLeaderId"),
                    partnerTeamLeader.nickname.as("partnerLeaderNickname"),
                    partnerTeamLeader.mbti.as("partnerLeaderMbti"),
                    partnerTeamLeader.profileImage.lowUrl.as("partnerLeaderLowProfileUrl"),
                    code.codeValue.as("partnerLeaderCollegeName"),
                    partnerTeamLeader.collegeInfo.collegeType.as("partnerLeaderCollegeType"),
                    partnerTeamLeader.collegeInfo.admissionYear.as("partnerLeaderAdmissionYear"),
                    partnerTeamLeader.profileImage.imageAuth.as("partnerLeaderImageAuth")
                ))
            .from(meeting)
            // My Team & Partner Team
            .join(meeting.team, team).on(team.deletedAt.isNull())
            .join(meeting.partnerTeam, partnerTeam)
            // Me & Partner Team Leader
            .join(team.teamLeader, member)
            .join(partnerTeam.teamLeader, partnerTeamLeader)
            // Partner Team Leader College
            .join(partnerTeamLeader.collegeInfo.collegeCode, code)
            .where(member.memberId.eq(memberId))
            .fetch();

        return meetingList.stream()
            .map(meetingInformation -> AcceptedMeetingResponseDto.of(
                meetingInformation, findTeamProfileImageUrl(meetingInformation.getTeamId())
            ))
            .sorted(Comparator.comparing(AcceptedMeetingResponseDto::getMeetingAcceptTime).reversed())
            .toList();
    }

    // 보낸 미팅 신청 조회
    @Override
    public List<SentMeetingResponseDto> findSentRequestList(final Long memberId) {
        List<MeetingRequestInformationDto> meetingRequestList = selectMeetingRequestInformationDto()
            .from(meetingRequest)
            // My Team & Partner Team
            .join(meetingRequest.team, team).on(team.deletedAt.isNull())
            .join(meetingRequest.partnerTeam, partnerTeam)
            // Me & Partner Team Leader
            .join(team.teamLeader, member)
            .join(partnerTeam.teamLeader, partnerTeamLeader)
            // Partner Team Leader College
            .join(partnerTeamLeader.collegeInfo.collegeCode, code)
            .where(member.memberId.eq(memberId))
            .fetch();

        return meetingRequestList.stream()
            .map(meetingRequestInformation -> SentMeetingResponseDto.of(
                meetingRequestInformation, findTeamProfileImageUrl(meetingRequestInformation.getTeamId())
            ))
            .toList();
    }

    // 받은 미팅 신청 조회
    @Override
    public List<ReceivedMeetingResponseDto> findReceiveRequestList(final Long memberId) {
        List<MeetingRequestInformationDto> meetingReceivedList = selectMeetingRequestInformationDto()
            .from(meetingRequest)
            // PartnerTeam == RequestReceivedTeam == My Team
            .join(meetingRequest.team, partnerTeam)
            .join(meetingRequest.partnerTeam, team).on(team.deletedAt.isNull())
            // Me & Partner Team Leader
            .join(team.teamLeader, member)
            .join(partnerTeam.teamLeader, partnerTeamLeader)
            // Partner Team Leader College
            .join(partnerTeamLeader.collegeInfo.collegeCode, code)
            .where(member.memberId.eq(memberId))
            .fetch();

        return meetingReceivedList.stream()
            .map(meetingRequestInformation -> ReceivedMeetingResponseDto.of(
                meetingRequestInformation, findTeamProfileImageUrl(meetingRequestInformation.getTeamId())
            ))
            .toList();
    }

    private JPAQuery<MeetingRequestInformationDto> selectMeetingRequestInformationDto() {
        return queryFactory.select(
            new QMeetingRequestInformationDto(
                meetingRequest.meetingRequestId,
                meetingRequest.createdAt.as("requestSentTime"),
                meetingRequest.message,
                meetingRequest.acceptStatus,
                partnerTeam.teamId,
                partnerTeam.memberNum.as("memberCount"),
                partnerTeam.region,
                partnerTeam.deletedAt,
                partnerTeamLeader.memberId.as("partnerLeaderId"),
                partnerTeamLeader.nickname.as("partnerLeaderNickname"),
                partnerTeamLeader.mbti.as("partnerLeaderMbti"),
                partnerTeamLeader.profileImage.lowUrl.as("partnerLeaderLowProfileUrl"),
                code.codeValue.as("partnerLeaderCollegeName"),
                partnerTeamLeader.collegeInfo.collegeType.as("partnerLeaderCollegeType"),
                partnerTeamLeader.collegeInfo.admissionYear.as("partnerLeaderAdmissionYear"),
                partnerTeamLeader.profileImage.imageAuth.as("partnerLeaderImageAuth")
            ));
    }

    private List<String> findTeamProfileImageUrl(final Long teamId) {
        return queryFactory.select(teamImage.teamImageUrl)
            .from(team)
            .join(teamImage).on(team.teamId.eq(teamImage.team.teamId))
            .where(team.teamId.eq(teamId))
            .orderBy(teamImage.sequence.asc())
            .fetch();
    }

    private Optional<TeamCheckProxyDto> findTeamCheckProxyByLeaderId(final Long leaderId) {
        return Optional.ofNullable(
            queryFactory
                .select(new QTeamCheckProxyDto(team.teamId, team.deletedAt))
                .from(team)
                .where(team.teamLeader.memberId.eq(leaderId))
                .fetchFirst()
        );
    }

    private Long getTeamIdWithCheckValid(final Optional<TeamCheckProxyDto> teamCheckProxy) {
        return teamCheckProxy
            .orElseThrow(TeamNotFoundException::new)
            .checkValid()
            .getTeamId();
    }

    private Optional<TeamCheckProxyDto> findTeamCheckProxyByTeamId(final Long teamId) {
        return Optional.ofNullable(
            queryFactory
                .select(new QTeamCheckProxyDto(team.teamId, team.deletedAt))
                .from(team)
                .where(team.teamId.eq(teamId))
                .fetchFirst()
        );
    }
}
