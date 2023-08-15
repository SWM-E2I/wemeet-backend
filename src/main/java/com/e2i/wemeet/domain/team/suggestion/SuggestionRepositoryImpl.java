package com.e2i.wemeet.domain.team.suggestion;

import static com.e2i.wemeet.domain.history.QHistory.history;
import static com.e2i.wemeet.domain.member.QMember.member;
import static com.e2i.wemeet.domain.team.QTeam.team;
import static com.e2i.wemeet.domain.team_image.QTeamImage.teamImage;

import com.e2i.wemeet.domain.member.data.Gender;
import com.e2i.wemeet.domain.team.data.suggestion.SuggestionTeamData;
import com.e2i.wemeet.domain.team.data.suggestion.TeamLeaderData;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.NumberExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@RequiredArgsConstructor
@Repository
public class SuggestionRepositoryImpl implements SuggestionRepository {

    private final JPAQueryFactory queryFactory;
    private static final int SUGGESTION_TEAM_LIMIT = 3;

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
            .orderBy(NumberExpression.random().asc())
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
            .orderBy(NumberExpression.random().asc())
            .limit(SUGGESTION_TEAM_LIMIT)
            .fetch();
    }
}
