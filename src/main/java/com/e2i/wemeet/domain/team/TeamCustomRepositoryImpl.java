package com.e2i.wemeet.domain.team;

import static com.e2i.wemeet.domain.code.QCode.code;
import static com.e2i.wemeet.domain.heart.QHeart.heart;
import static com.e2i.wemeet.domain.meeting.QMeetingRequest.meetingRequest;
import static com.e2i.wemeet.domain.member.QMember.member;
import static com.e2i.wemeet.domain.team.QTeam.team;
import static com.e2i.wemeet.domain.team_image.QTeamImage.teamImage;
import static com.e2i.wemeet.domain.team_member.QTeamMember.teamMember;
import static com.e2i.wemeet.service.heart.HeartServiceImpl.HEART_EXPIRE_DAY;
import static com.e2i.wemeet.service.meeting.MeetingHandleServiceImpl.MEETING_REQUEST_EXPIRE_DAY;

import com.e2i.wemeet.domain.team.data.TeamImageData;
import com.e2i.wemeet.dto.dsl.TeamInformationDto;
import com.e2i.wemeet.dto.dsl.TeamMemberInformationDto;
import com.e2i.wemeet.dto.response.LeaderResponseDto;
import com.e2i.wemeet.exception.notfound.TeamNotFoundException;
import com.querydsl.core.types.Projections;
import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@RequiredArgsConstructor
@Repository
public class TeamCustomRepositoryImpl implements TeamCustomRepository {

    private final JPAQueryFactory queryFactory;
    private static final LocalTime boundaryTime = LocalTime.of(23, 11);

    @Override
    public List<TeamImageData> findTeamImagesByTeamId(final Long teamId) {
        return queryFactory
            .select(Projections.constructor(TeamImageData.class, teamImage.teamImageUrl.as("url")))
            .from(teamImage)
            .where(teamImage.team.teamId.eq(teamId))
            .orderBy(teamImage.sequence.asc())
            .fetch();
    }

    @Override
    public Optional<LeaderResponseDto> findLeaderByTeamId(final Long teamId) {
        return Optional.ofNullable(queryFactory
            .select(Projections.constructor(LeaderResponseDto.class,
                    member.memberId.as("leaderId"),
                    member.nickname,
                    member.mbti,
                    code.codeValue.as("collegeName"),
                    member.collegeInfo.collegeType.as("collegeType"),
                    member.collegeInfo.admissionYear,
                    member.profileImage.lowUrl.as("leaderLowProfileImageUrl"),
                    member.profileImage.imageAuth.as("imageAuth")
                )
            )
            .from(team)
            .join(team.teamLeader, member)
            .join(member.collegeInfo.collegeCode, code)
            .where(team.teamId.eq(teamId))
            .fetchOne());
    }

    @Override
    public Optional<TeamInformationDto> findTeamInformationByTeamId(final Long memberLeaderId, final Long teamId, final LocalDateTime readTime) {
        com.e2i.wemeet.domain.team.QTeam myTeam = new com.e2i.wemeet.domain.team.QTeam("myTeam");
        TeamInformationDto teamInformationDto = Optional.ofNullable(queryFactory
                .select(Projections.constructor(TeamInformationDto.class,
                    team.teamId,
                    team.chatLink,
                    team.memberNum,
                    team.region,
                    team.drinkRate,
                    team.drinkWithGame,
                    team.additionalActivity,
                    team.introduction,
                    team.deletedAt,
                    // 미팅 요청 조회
                    JPAExpressions
                        .select(meetingRequest.acceptStatus)
                        .from(meetingRequest)
                        .where(meetingRequest.team.eq(myTeam),
                            meetingRequest.partnerTeam.eq(team),
                            meetingRequest.createdAt.goe(readTime.minusDays(MEETING_REQUEST_EXPIRE_DAY))
                        )
                        .orderBy(meetingRequest.createdAt.desc())
                        .limit(1),
                    // 좋아요 표시 여부 조회
                    JPAExpressions
                        .select(heart.heartId)
                        .from(heart)
                        .where(heart.team.eq(myTeam),
                            heart.partnerTeam.eq(team),
                            heart.createdAt.goe(readTime.minusDays(HEART_EXPIRE_DAY))
                        )
                        .limit(1),
                    myTeam.teamId
                ))
                .from(team)
                .leftJoin(myTeam).on(
                    myTeam.teamLeader.memberId.eq(memberLeaderId),
                    myTeam.deletedAt.isNull()
                )
                .where(team.teamId.eq(teamId))
                .fetchOne())
            .orElseThrow(TeamNotFoundException::new);

        List<TeamMemberInformationDto> teamMemberResponseDtos = findTeamMemberResponseDto(teamId);
        if (teamMemberResponseDtos.isEmpty()) {
            throw new TeamNotFoundException();
        }
        teamInformationDto.setTeamMembers(teamMemberResponseDtos);

        return Optional.of(teamInformationDto);
    }

    private List<TeamMemberInformationDto> findTeamMemberResponseDto(final Long teamId) {
        return queryFactory
            .select(Projections.constructor(TeamMemberInformationDto.class,
                code.codeValue.as("collegeName"),
                teamMember.collegeInfo.collegeType,
                teamMember.collegeInfo.admissionYear,
                teamMember.mbti
            ))
            .from(teamMember)
            .join(teamMember.collegeInfo.collegeCode, code)
            .join(teamMember.team, team)
            .where(team.teamId.eq(teamId))
            .fetch();
    }
}
