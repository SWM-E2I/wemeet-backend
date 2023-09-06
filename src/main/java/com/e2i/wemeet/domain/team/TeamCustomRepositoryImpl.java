package com.e2i.wemeet.domain.team;

import static com.e2i.wemeet.domain.heart.QHeart.heart;
import static com.e2i.wemeet.domain.member.QMember.member;
import static com.e2i.wemeet.domain.team.QTeam.team;
import static com.e2i.wemeet.domain.team_image.QTeamImage.teamImage;

import com.e2i.wemeet.domain.team.data.TeamImageData;
import com.e2i.wemeet.domain.team.data.suggestion.TeamLeaderData;
import com.e2i.wemeet.dto.dsl.HeartTeamData;
import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@RequiredArgsConstructor
@Repository
public class TeamCustomRepositoryImpl implements TeamCustomRepository {

    private final JPAQueryFactory queryFactory;
    private static final LocalTime boundaryTime = LocalTime.of(23, 11);

    @Override
    public List<TeamImageData> findTeamWithTeamImages(Long teamId) {
        return queryFactory
            .select(Projections.constructor(TeamImageData.class, teamImage.teamImageUrl.as("url")))
            .from(teamImage)
            .where(teamImage.team.teamId.eq(teamId))
            .orderBy(teamImage.sequence.asc())
            .fetch();
    }

    public List<HeartTeamData> findSentHeart(Long teamId,
        LocalDateTime requestedTime) {
        LocalDateTime boundaryDateTime = requestedTime.with(boundaryTime);

        if (requestedTime.isBefore(boundaryDateTime)) {
            boundaryDateTime = boundaryDateTime.minusDays(1);
        }

        return queryFactory
            .select(
                Projections.constructor(HeartTeamData.class, heart.partnerTeam.teamId,
                    heart.partnerTeam.memberNum,
                    heart.partnerTeam.region, teamImage.teamImageUrl.as("teamMainImageUrl"),
                    heart.createdAt,
                    Projections.constructor(TeamLeaderData.class, member.nickname,
                        member.mbti,
                        member.profileImage.basicUrl.as("profileImageUrl"),
                        member.collegeInfo.collegeCode.codeValue.as("college"))))
            .from(heart)
            .join(team)
            .on(heart.team.teamId.eq(team.teamId))
            .join(heart.partnerTeam.teamLeader, member)
            .on(heart.partnerTeam.teamLeader.memberId.eq(member.memberId))
            .join(teamImage)
            .on(teamImage.team.teamId.eq(heart.partnerTeam.teamId))
            .where(team.teamId.eq(teamId))
            .where(team.deletedAt.isNull())
            .where(heart.createdAt.between(boundaryDateTime, requestedTime))
            .where(teamImage.sequence.eq(1))
            .fetch();
    }

    public List<HeartTeamData> findReceivedHeart(Long teamId,
        LocalDateTime requestedTime) {
        LocalDateTime boundaryDateTime = requestedTime.with(boundaryTime);

        if (requestedTime.isBefore(boundaryDateTime)) {
            boundaryDateTime = boundaryDateTime.minusDays(1);
        }

        return queryFactory
            .select(
                Projections.constructor(HeartTeamData.class, heart.team.teamId,
                    heart.team.memberNum,
                    heart.team.region, teamImage.teamImageUrl.as("teamMainImageUrl"),
                    heart.createdAt,
                    Projections.constructor(TeamLeaderData.class, member.nickname, member.mbti,
                        member.profileImage.basicUrl.as("profileImageUrl"),
                        member.collegeInfo.collegeCode.codeValue.as("college"))))
            .from(heart)
            .join(team)
            .on(heart.partnerTeam.teamId.eq(team.teamId))
            .join(heart.team.teamLeader, member)
            .on(heart.team.teamLeader.memberId.eq(member.memberId))
            .join(teamImage)
            .on(teamImage.team.teamId.eq(heart.team.teamId))
            .where(team.teamId.eq(teamId))
            .where(team.deletedAt.isNull())
            .where(heart.createdAt.between(boundaryDateTime, requestedTime))
            .where(teamImage.sequence.eq(1))
            .fetch();
    }
}
