package com.e2i.wemeet.domain.team;

import static com.e2i.wemeet.domain.code.QCode.code;
import static com.e2i.wemeet.domain.member.QMember.member;
import static com.e2i.wemeet.domain.team.QTeam.team;
import static com.e2i.wemeet.domain.team_image.QTeamImage.teamImage;
import static com.e2i.wemeet.domain.team_member.QTeamMember.teamMember;

import com.e2i.wemeet.domain.team.data.TeamImageData;
import com.e2i.wemeet.dto.dsl.TeamInformationDto;
import com.e2i.wemeet.dto.dsl.TeamMemberInformationDto;
import com.e2i.wemeet.dto.response.LeaderResponseDto;
import com.e2i.wemeet.exception.notfound.TeamNotFoundException;
import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@RequiredArgsConstructor
@Repository
public class TeamCustomRepositoryImpl implements TeamCustomRepository {

    private final JPAQueryFactory queryFactory;

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
    public Optional<TeamInformationDto> findTeamInformationByTeamId(final Long teamId) {
        TeamInformationDto teamInformationDto = Optional.ofNullable(queryFactory
                .select(Projections.constructor(TeamInformationDto.class,
                    team.teamId,
                    team.memberNum,
                    team.region,
                    team.drinkRate,
                    team.drinkWithGame,
                    team.additionalActivity,
                    team.introduction,
                    team.deletedAt
                ))
                .from(team)
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
