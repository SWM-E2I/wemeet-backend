package com.e2i.wemeet.domain.team.suggestion;

import static com.e2i.wemeet.domain.history.QHistory.history;
import static com.e2i.wemeet.domain.member.QMember.member;
import static com.e2i.wemeet.domain.team.QTeam.team;
import static com.e2i.wemeet.domain.team_image.QTeamImage.teamImage;

import com.e2i.wemeet.domain.history.History;
import com.e2i.wemeet.domain.member.data.Gender;
import com.e2i.wemeet.domain.team.data.suggestion.SuggestionHistoryData;
import com.e2i.wemeet.domain.team.data.suggestion.SuggestionTeamData;
import com.e2i.wemeet.domain.team.data.suggestion.TeamLeaderData;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@RequiredArgsConstructor
@Repository
public class SuggestionRepositoryImpl implements SuggestionRepository {

    private final JPAQueryFactory queryFactory;
    private static final int SUGGESTION_TEAM_LIMIT = 3;
    private static final LocalTime boundaryTime = LocalTime.of(23, 11);

    @Override
    public List<SuggestionTeamData> findSuggestionTeamForTeamLeader(Long memberId, int memberNum,
        Gender gender) {
        List<Long> suggestionHistory = queryFactory
            .select(history.team.teamId)
            .from(history)
            .where(history.member.memberId.eq(memberId))
            .fetch();

        return queryFactory
            .select(Projections.constructor(SuggestionTeamData.class, team,
                teamImage.teamImageUrl.as("teamMainImageUrl"),
                Projections.constructor(TeamLeaderData.class, member.nickname, member.mbti,
                    member.profileImage.basicUrl.as("profileImageUrl"),
                    member.collegeInfo.collegeCode.codeValue.as("college"))))
            .from(team)
            .join(team.teamLeader, member)
            .on(team.teamLeader.memberId.eq(member.memberId))
            .join(teamImage)
            .on(teamImage.team.teamId.eq(team.teamId))
            .where(team.deletedAt.isNull())
            .where(team.gender.ne(gender))
            .where(team.teamId.notIn(suggestionHistory))
            .where(team.memberNum.eq(memberNum))
            .where(teamImage.sequence.eq(1))
            .orderBy(Expressions.numberTemplate(Double.class, "function('rand')").asc())
            .limit(SUGGESTION_TEAM_LIMIT)
            .fetch();
    }

    @Override
    public List<SuggestionTeamData> findSuggestionTeamForUser(Long memberId, Gender gender) {
        List<Long> suggestionHistory = queryFactory
            .select(history.team.teamId)
            .from(history)
            .where(history.member.memberId.eq(memberId))
            .fetch();

        return queryFactory
            .select(Projections.constructor(SuggestionTeamData.class, team,
                teamImage.teamImageUrl.as("teamMainImageUrl"),
                Projections.constructor(TeamLeaderData.class, member.nickname, member.mbti,
                    member.profileImage.basicUrl.as("profileImageUrl"),
                    member.collegeInfo.collegeCode.codeValue.as("college"))))
            .from(team)
            .join(team.teamLeader, member)
            .on(team.teamLeader.memberId.eq(member.memberId))
            .join(teamImage)
            .on(teamImage.team.teamId.eq(team.teamId))
            .where(team.deletedAt.isNull())
            .where(team.gender.ne(gender))
            .where(team.teamId.notIn(suggestionHistory))
            .where(teamImage.sequence.eq(1))
            .orderBy(Expressions.numberTemplate(Double.class, "function('rand')").asc())
            .limit(SUGGESTION_TEAM_LIMIT)
            .fetch();
    }

    public List<History> findHistory(Long memberId, LocalDateTime requestedTime) {
        LocalDateTime boundaryDateTime = requestedTime.with(boundaryTime);

        if (requestedTime.isBefore(boundaryDateTime)) {
            boundaryDateTime = boundaryDateTime.minusDays(1);
        }

        return queryFactory.selectFrom(history)
            .where(history.member.memberId.eq(memberId))
            .where(history.createdAt.between(boundaryDateTime, requestedTime))
            .fetch();
    }

    public List<SuggestionHistoryData> findSuggestionHistoryTeam(Long memberId,
        LocalDateTime requestedTime) {
        LocalDateTime boundaryDateTime = requestedTime.with(boundaryTime);

        if (requestedTime.isBefore(boundaryDateTime)) {
            boundaryDateTime = boundaryDateTime.minusDays(1);
        }

        return queryFactory
            .select(
                Projections.constructor(SuggestionHistoryData.class, team.teamId, team.memberNum,
                    team.region, teamImage.teamImageUrl.as("teamMainImageUrl"), history.isLiked,
                    Projections.constructor(TeamLeaderData.class, member.nickname, member.mbti,
                        member.profileImage.basicUrl.as("profileImageUrl"),
                        member.collegeInfo.collegeCode.codeValue.as("college"))))
            .from(history)
            .join(team)
            .on(history.team.teamId.eq(team.teamId))
            .join(team.teamLeader, member)
            .on(team.teamLeader.memberId.eq(member.memberId))
            .join(teamImage)
            .on(teamImage.team.teamId.eq(team.teamId))
            .where(history.member.memberId.eq(memberId))
            .where(history.createdAt.between(boundaryDateTime, requestedTime))
            .where(team.deletedAt.isNull())
            .where(teamImage.sequence.eq(1))
            .fetch();
    }
}
